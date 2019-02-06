package no.nav.tag.kontakt.oss.gsak.integrasjon;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.KontaktskjemaException;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class GsakKlient {
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
        ResponseEntity<GsakRespons> respons = restTemplate.postForEntity(
                gsakUrl,
                lagGsakRequestEntity(gsakRequest),
                GsakRespons.class
        );

        if (HttpStatus.CREATED.equals(respons.getStatusCode())) {
            Integer id = respons.getBody().getId();
            log.info("Gsak-oppgave med id={} opprettet.", id);
            return id;
        } else {
            throw new KontaktskjemaException("Kall til Gsak returnerte ikke 201 CREATED");
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

    @lombok.Value
    public static class GsakRespons {
        private Integer id;
    }
}
