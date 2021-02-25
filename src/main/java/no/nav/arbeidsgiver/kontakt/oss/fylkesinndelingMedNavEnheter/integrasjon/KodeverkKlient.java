package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.arbeidsgiver.kontakt.oss.KontaktskjemaException;
import no.nav.arbeidsgiver.kontakt.oss.config.IgnoreAllErrors;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.Bydel;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.Kommune;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Service
public class KodeverkKlient {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final RestTemplate restTemplate;
    private final String kodeverkUrl;

    public KodeverkKlient(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${kodeverk.url}") String kodeverkUrl
    ) {
        this.restTemplate = restTemplateBuilder.errorHandler(new IgnoreAllErrors()).build();
        this.kodeverkUrl = kodeverkUrl;
    }

    public List<Kommune> hentKommuner() {
        return oversettTilKommuner(hentKodeverkBetydninger("Kommuner"));
    }

    public List<Bydel> hentBydeler() {
        return oversettTilBydeler(hentKodeverkBetydninger("Bydeler"));
    }

    private ResponseEntity<String> hentKodeverkBetydninger(String kodeverksnavn) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Nav-Call-Id", UUID.randomUUID().toString());
        headers.set("Nav-Consumer-Id", "kontakt-oss-api");
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = String.format(
                "%s/kodeverk/%s/koder/betydninger?ekskluderUgyldige=true&spraak=nb",
                kodeverkUrl,
                kodeverksnavn
        );

        ResponseEntity<String> jsonResponse = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        if (HttpStatus.OK.equals(jsonResponse.getStatusCode())) {
            return jsonResponse;
        } else {
            throw new KontaktskjemaException("Kall til NORG returnerte ikke 200 OK. Returverdi: " + jsonResponse.getBody());
        }

    }

    private List<Kommune> oversettTilKommuner(ResponseEntity<String> jsonResponse) {
        List<Kommune> kommuner = new ArrayList<>();

        hentMapFraKodeTilTerm(jsonResponse).forEach((kommuneNr, navn) -> kommuner.add(
                new Kommune(kommuneNr, navn)
        ));

        return kommuner;
    }

    private List<Bydel> oversettTilBydeler(ResponseEntity<String> jsonResponse) {
        List<Bydel> kommuner = new ArrayList<>();

        hentMapFraKodeTilTerm(jsonResponse).forEach((bydelNr, navn) -> kommuner.add(
                new Bydel(bydelNr, navn)
        ));

        return kommuner;
    }

    private Map<String, String> hentMapFraKodeTilTerm(ResponseEntity<String> jsonResponse) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonResponse.getBody());
            return hentMapFraKodeTilTerm(jsonNode);
        } catch (IOException e) {
            throw new KontaktskjemaException("Returverdi fra Felles Kodeverk er ikke riktig formatert JSON, eller passer ikke med vår modell. Returverdi: " + jsonResponse.getBody());
        }
    }

    private Map<String, String> hentMapFraKodeTilTerm(JsonNode jsonNode) {
        JsonNode betydninger = jsonNode.get("betydninger");

        Map<String, String> map = new TreeMap<>();

        List<String> koder = new ArrayList<>();
        betydninger.fieldNames().forEachRemaining(koder::add);

        koder.forEach(kode -> {
            JsonNode betydning = betydninger.get(kode).get(0);
            if (betydning != null) {
                String term = betydning.get("beskrivelser").get("nb").get("term").textValue();
                map.put(kode, term);
            }
        });

        return map;
    }
}
