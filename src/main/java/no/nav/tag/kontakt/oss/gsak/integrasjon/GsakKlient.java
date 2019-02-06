package no.nav.tag.kontakt.oss.gsak.integrasjon;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.KontaktskjemaException;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@Slf4j
public class GsakKlient {
    private final RestTemplate restTemplate;

    @Value("${GSAK_URL:default}")
    private String gsakUrl;

    @Autowired
    public GsakKlient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Integer opprettGsakOppgave(GsakRequest gsakInnsending) {
        ResponseEntity<GsakInnsendingRespons> respons = restTemplate.postForEntity(
                gsakUrl,
                lagGsakRequestEntity(gsakInnsending),
                GsakInnsendingRespons.class
        );

        if (HttpStatus.CREATED.equals(respons.getStatusCode())) {
            Integer id = respons.getBody().getId();
            log.info("Gsak-oppgave med id={} opprettet.", id);
            return id;
        } else {
            throw new KontaktskjemaException("Kall til Gsak returnerte ikke 201 CREATED");
        }
    }

    private HttpEntity<GsakRequest> lagGsakRequestEntity(GsakRequest gsakInnsending) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Correlation-ID", MDC.get("correlationId"));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(gsakInnsending, headers);
    }

    @Data
    private static class GsakInnsendingRespons {
        private Integer id;
    }
}
