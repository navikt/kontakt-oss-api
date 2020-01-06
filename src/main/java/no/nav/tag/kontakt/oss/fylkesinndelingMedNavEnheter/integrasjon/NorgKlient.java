package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.KontaktskjemaException;
import no.nav.tag.kontakt.oss.config.IgnoreAllErrors;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.KommuneEllerBydel;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.NavEnhet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class NorgKlient {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final RestTemplate restTemplate;
    private final String norgUrl;

    public NorgKlient(
            RestTemplate restTemplate,
            @Value("${norg.url}") String norgUrl
    ) {
        restTemplate.setErrorHandler(new IgnoreAllErrors());
        this.restTemplate = restTemplate;
        this.norgUrl = norgUrl;
    }

    public List<NorgOrganisering> hentOrganiseringFraNorg() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("consumerId", "kontakt-oss-api");
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> jsonResponse = restTemplate.exchange(
                norgUrl + "/enhet/kontaktinformasjon/organisering/AKTIV",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        if (HttpStatus.OK.equals(jsonResponse.getStatusCode())) {
            return oversettTilNorgOrganisering(jsonResponse);
        } else {
            throw new KontaktskjemaException("Kall til NORG returnerte ikke 200 OK. Returverdi: " + jsonResponse.getBody());
        }
    }

    private List<NorgOrganisering> oversettTilNorgOrganisering(ResponseEntity<String> jsonResponse) {
        try {
            List<NorgOrganisering> norgOrganisering = new ArrayList<>();

            JsonNode jsonNode = objectMapper.readTree(jsonResponse.getBody());
            jsonNode.forEach(organisering -> norgOrganisering.add(
                    new NorgOrganisering(
                            organisering.get("enhet").get("enhetNr").textValue(),
                            organisering.get("enhet").get("status").textValue(),
                            organisering.get("overordnetEnhet").textValue()
                    )
            ));

            return norgOrganisering;
        } catch (IOException e) {
            throw new KontaktskjemaException("Returverdi fra NORG er ikke riktig formatert JSON, eller passer ikke med vår modell. Returverdi: " + jsonResponse.getBody());
        }
    }

    public Map<KommuneEllerBydel, NavEnhet> hentMapFraKommuneEllerBydelTilNavenhet(List<KommuneEllerBydel> kommunerOgBydeler) {
        Map<KommuneEllerBydel, NavEnhet> map = new HashMap<>();

        for (KommuneEllerBydel kommuneEllerBydel : kommunerOgBydeler) {
            Optional<NavEnhet> enhetsnrOptional = hentTilhoerendeNavEnhet(kommuneEllerBydel.getNummer());
            enhetsnrOptional.ifPresent(enhetsnr -> map.put(kommuneEllerBydel, enhetsnr));
        }

        return map;
    }

    public Optional<NavEnhet> hentTilhoerendeNavEnhet(String kommunenrEllerBydelsnr) {
        ResponseEntity<String> jsonResponse = restTemplate.getForEntity(
                norgUrl + "/enhet/navkontor/" + kommunenrEllerBydelsnr,
                String.class
        );

        switch (jsonResponse.getStatusCode()) {
            case OK:
                String enhetsnr = oversettTilEnhetsnr(jsonResponse);
                log.info("Funnet tilhørende enhetsnr {} for kommune/bydel {}", enhetsnr, kommunenrEllerBydelsnr);
                return Optional.of(new NavEnhet(enhetsnr));
            case NOT_FOUND:
                log.info("Fant ikke tilhørende enhetsnr for kommune/bydel {}", kommunenrEllerBydelsnr);
                return Optional.empty();
            default:
                throw new KontaktskjemaException(
                        "Kall til NORG for bydel/fylke " + kommunenrEllerBydelsnr + " feilet. Returverdi: " + jsonResponse.getBody()
                );
        }
    }

    private String oversettTilEnhetsnr(ResponseEntity<String> jsonResponse) {
        try {
            return objectMapper.readTree(jsonResponse.getBody()).get("enhetNr").textValue();
        } catch (IOException e) {
            throw new KontaktskjemaException("Returverdi fra NORG er ikke riktig formatert JSON, eller passer ikke med vår modell. Returverdi: " + jsonResponse.getBody());
        }
    }
}
