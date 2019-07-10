package no.nav.tag.kontakt.oss.gsak.integrasjon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.BadRequestException;
import no.nav.tag.kontakt.oss.KontaktskjemaException;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
@Slf4j
public class GsakKlient {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final RestTemplate restTemplate;
    private final String gsakUrl;

    @Autowired
    public GsakKlient(
            RestTemplate restTemplate,
            @Value("${gsak.url}") String gsakUrl
    ) {
        this.restTemplate = restTemplate;
        this.gsakUrl = gsakUrl;
    }

    public Integer opprettGsakOppgave(GsakRequest gsakRequest) {
        ResponseEntity<String> jsonResponse = restTemplate.postForEntity(
                gsakUrl,
                lagGsakRequestEntity(gsakRequest),
                String.class
        );

        if (HttpStatus.CREATED.equals(jsonResponse.getStatusCode())) {
            return hentIdFraRespons(jsonResponse);

        } else if (HttpStatus.BAD_REQUEST.equals(jsonResponse.getStatusCode())) {
            throw new BadRequestException("Gsak returnerte 400 BAD REQUEST. Returverdi: " + jsonResponse.getBody());

        } else {
            throw new KontaktskjemaException("Kall til Gsak returnerte ikke 201 CREATED. Statuskode: " + jsonResponse.getStatusCode() + ". Returverdi: " + jsonResponse.getBody());
        }
    }

    private Integer hentIdFraRespons(ResponseEntity<String> respons) {
        try {
            JsonNode jsonNode = objectMapper.readTree(respons.getBody());
            return jsonNode.get("id").asInt();
        } catch (IOException e) {
            log.error("Returverdi: " + respons.getBody());
            throw new KontaktskjemaException("Returverdi fra Gsak er ikke riktig formatert JSON");
        }
    }

    private HttpEntity<GsakRequest> lagGsakRequestEntity(GsakRequest gsakRequest) {
        String correlationId = MDC.get("correlationId");
        if (correlationId == null) {
            throw new KontaktskjemaException("X-Correlation-ID er ikke satt");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Correlation-ID", correlationId);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(gsakRequest, headers);
    }
}
