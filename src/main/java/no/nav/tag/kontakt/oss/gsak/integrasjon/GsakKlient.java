package no.nav.tag.kontakt.oss.gsak.integrasjon;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            @Value("${GSAK_URL:default}") String gsakUrl
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
            try {
                GsakRespons respons = objectMapper.readValue(jsonResponse.getBody(), GsakRespons.class);
                return respons.getId();
            } catch (IOException e) {
                log.error("Returverdi: " + jsonResponse.getBody());
                throw new KontaktskjemaException("Returverdi fra Gsak er ikke riktig formatert JSON");
            }
        } else {
            throw new KontaktskjemaException("Kall til Gsak returnerte ikke 201 CREATED. Returverdi: " + jsonResponse.getBody());
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GsakRespons {
        private Integer id;
    }
}
