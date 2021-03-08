package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon;

import lombok.extern.slf4j.Slf4j;
import no.nav.arbeidsgiver.kontakt.oss.KontaktskjemaException;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.KommuneEllerBydel;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.NavEnhet;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
public class NorgKlient {

    private final RestTemplate restTemplate;
    private final String norgUrl;

    public NorgKlient(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${norg.url}") String norgUrl
    ) {
        this.restTemplate = restTemplateBuilder.errorHandler(new DefaultResponseErrorHandler() {
            @Override
            protected boolean hasError(@NonNull HttpStatus statusCode) {
                return !statusCode.equals(HttpStatus.NOT_FOUND) && super.hasError(statusCode);
            }

            @Override
            protected void handleError(
                    @NonNull ClientHttpResponse response,
                    @NonNull HttpStatus statusCode
            ) throws IOException {
                throw new KontaktskjemaException(response.getStatusText());
            }
        }).build();
        this.norgUrl = norgUrl;
    }

    public List<NorgOrganisering> hentOrganiseringFraNorg() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("consumerId", "kontakt-oss-api");
        headers.setContentType(MediaType.APPLICATION_JSON);

        return restTemplate.exchange(
                norgUrl + "/enhet/kontaktinformasjon/organisering/AKTIV",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<NorgOrganisering>>() {
                }
        ).getBody();
    }

    public Map<KommuneEllerBydel, NavEnhet> hentMapFraKommuneEllerBydelTilNavenhet(
            List<KommuneEllerBydel> kommunerOgBydeler
    ) {
        return kommunerOgBydeler.stream()
                .map(kommuneEllerBydel ->
                        hentTilhoerendeNavEnhet(kommuneEllerBydel.getNummer())
                                .map((enhetsnr) -> Pair.of(kommuneEllerBydel, enhetsnr))
                )
                .filter(Optional::isPresent).map(Optional::get)
                .collect(toMap(
                        Pair::getKey,
                        Pair::getValue
                ));
    }

    public Optional<NavEnhet> hentTilhoerendeNavEnhet(String kommunenrEllerBydelsnr) {
        return Optional.ofNullable(
                restTemplate.getForEntity(
                        norgUrl + "/enhet/navkontor/" + kommunenrEllerBydelsnr,
                        NavEnhet.class
                ).getBody()
        );
    }
}
