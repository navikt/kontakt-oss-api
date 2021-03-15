package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon;

import com.fasterxml.jackson.databind.JsonNode;
import no.nav.arbeidsgiver.kontakt.oss.KontaktskjemaException;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.Bydel;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.Kommune;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

@Service
public class KodeverkKlient {
    private final RestTemplate restTemplate;
    private final String kodeverkUrl;

    public KodeverkKlient(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${kodeverk.url}") String kodeverkUrl
    ) {
        this.restTemplate = restTemplateBuilder.errorHandler(new DefaultResponseErrorHandler() {
            @Override
            protected boolean hasError(@NonNull HttpStatus statusCode) {
                return !statusCode.equals(HttpStatus.OK) || super.hasError(statusCode);
            }

            @Override
            protected void handleError(
                    @NonNull ClientHttpResponse response,
                    @NonNull HttpStatus statusCode
            ) throws IOException {
                throw new KontaktskjemaException("Kall til kodeverk returnerte ikke 200 OK. Returverdi: " + statusCode.toString());
            }
        }).build();
        this.kodeverkUrl = kodeverkUrl;
    }

    public List<Kommune> hentKommuner() {
        return hentKodeverkBetydninger("Kommuner", Kommune::new);
    }

    public List<Bydel> hentBydeler() {
        return hentKodeverkBetydninger("Bydeler", Bydel::new);
    }

    private <T> List<T> hentKodeverkBetydninger(
            String kodeverksnavn,
            BiFunction<String, String, T> create
    ) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Nav-Call-Id", UUID.randomUUID().toString());
            headers.set("Nav-Consumer-Id", "kontakt-oss-api");
            headers.setContentType(MediaType.APPLICATION_JSON);

            String url = String.format(
                    "%s/kodeverk/%s/koder/betydninger?ekskluderUgyldige=true&spraak=nb",
                    kodeverkUrl,
                    kodeverksnavn
            );

            JsonNode jsonNode = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    JsonNode.class
            ).getBody();

            if (jsonNode == null) {
                throw new KontaktskjemaException("Finner ikke body i kodeverk-response");
            }

            List<T> list = new ArrayList<>();
            jsonNode.get("betydninger").fields().forEachRemaining(entry -> {
                String kode = entry.getKey();
                String betydning = entry.getValue().at("/0/beskrivelser/nb/term").textValue();
                if (betydning != null) {
                    list.add(create.apply(kode, betydning));
                }
            });

            return list;
        } catch (Exception e) {
            throw new KontaktskjemaException("Feil ved henting av fra kodeverk", e);
        }
    }
}
