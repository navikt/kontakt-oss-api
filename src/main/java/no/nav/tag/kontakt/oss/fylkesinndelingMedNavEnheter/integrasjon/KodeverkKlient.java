package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.KontaktskjemaException;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.Kommune;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class KodeverkKlient {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final RestTemplate restTemplate;
    private final String kodeverkUrl;

    public KodeverkKlient(
            RestTemplate restTemplate,
            @Value("${kodeverk.url}") String kodeverkUrl
    ) {
        restTemplate.setErrorHandler(new IgnoreAllErrors());
        this.restTemplate = restTemplate;
        this.kodeverkUrl = kodeverkUrl;
    }

    public List<Kommune> hentKommuner() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Nav-Call-Id", UUID.randomUUID().toString());
        headers.set("Nav-Consumer-Id", "kontakt-oss-api");
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> jsonResponse = restTemplate.exchange(
                this.kodeverkUrl + "/kodeverk/Kommuner/koder/betydninger",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        if (HttpStatus.OK.equals(jsonResponse.getStatusCode())) {
            return oversettTilKommuner(jsonResponse);
        } else {
            throw new KontaktskjemaException("Kall til NORG returnerte ikke 200 OK. Returverdi: " + jsonResponse.getBody());
        }
    }

    private List<Kommune> oversettTilKommuner(ResponseEntity<String> jsonResponse) {
        try {
            List<Kommune> kommuner = new ArrayList<>();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse.getBody());


            jsonNode.get("betydninger").fields().forEachRemaining(betydning -> {
                if (!betydning.getValue().isNull()) {
                    kommuner.add(new Kommune(
                            betydning.getKey(),
                            betydning.getValue().get("beskrivelser").get("nb").get("term").textValue()
                    ));
                }
            });
            return kommuner;
        } catch (IOException e) {
            throw new KontaktskjemaException("Returverdi fra Felles Kodeverk er ikke riktig formatert JSON, eller passer ikke med vår modell. Returverdi: " + jsonResponse.getBody());
        }
    }
}
