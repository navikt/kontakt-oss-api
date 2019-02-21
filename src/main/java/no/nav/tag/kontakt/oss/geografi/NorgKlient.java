package no.nav.tag.kontakt.oss.geografi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.KontaktskjemaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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

    public Geografi hentGeografiFraNorg() {
        ResponseEntity<String> jsonResponse = restTemplate.getForEntity(
                norgUrl + "/kodeverk/geografi",
                String.class
        );

        if (HttpStatus.OK.equals(jsonResponse.getStatusCode())) {
            return new Geografi(oversettTilNorgGeografi(jsonResponse));
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

}
