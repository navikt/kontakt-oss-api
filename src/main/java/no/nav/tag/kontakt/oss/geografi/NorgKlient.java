package no.nav.tag.kontakt.oss.geografi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.KontaktskjemaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NorgKlient {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final RestTemplate restTemplate;
    private final String norgUrl;

    public NorgKlient(
            RestTemplate restTemplate,
            @Value("${NORG_URL:default}") String norgUrl
    ) {
        this.restTemplate = restTemplate;
        this.norgUrl = norgUrl;
    }

    public FylkesinndelingMedNavenheter hentGeografiFraNorg() {
        ResponseEntity<String> jsonResponse = restTemplate.getForEntity(
                norgUrl + "/kodeverk/geografi",
                String.class
        );

        if (HttpStatus.OK.equals(jsonResponse.getStatusCode())) {
            return new FylkesinndelingMedNavenheter(oversettTilNorgGeografi(jsonResponse));
        } else {
            throw new KontaktskjemaException("Kall til NORG returnerte ikke 200 OK. Returverdi: " + jsonResponse.getBody());
        }
    }

    private List<NorgGeografi> oversettTilNorgGeografi(ResponseEntity<String> jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse.getBody(), new TypeReference<List<NorgGeografi>>() {});
        } catch (IOException e) {
            throw new KontaktskjemaException("Returverdi fra NORG er ikke riktig formatert JSON, eller passer ikke med vår modell. Returverdi: " + jsonResponse.getBody());
        }
    }

    public List<NorgOrganisering> hentOrganiseringFraNorg() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("consumerId", "kontakt-oss-api");
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> jsonResponse = restTemplate.exchange(
                norgUrl + "/enhet/kontaktinformasjon/organisering/all",
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

}
