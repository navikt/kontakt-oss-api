package no.nav.tag.kontakt.oss.gsak;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.KontaktskjemaException;
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

    @Value("${GSAK_URL:}")
    private String gsakUrl;

    @Autowired
    public GsakKlient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Integer opprettGsakOppgave(GsakInnsending gsakInnsending) {
        String correlationId = UUID.randomUUID().toString();

        ResponseEntity<GsakInnsendingRespons> respons = restTemplate.postForEntity(
                gsakUrl,
                lagGsakRequestEntity(gsakInnsending, correlationId),
                GsakInnsendingRespons.class
        );

        validerRespons(respons);

        Integer id = respons.getBody().getId();
        log.info("Gsak-oppgave med id={} opprettet. X-Correlation-ID={}", 1, correlationId);
        return id;
    }

    private void validerRespons(ResponseEntity<GsakInnsendingRespons> gsakRespons) {
        if (!HttpStatus.CREATED.equals(gsakRespons.getStatusCode())) {
            throw new KontaktskjemaException("Kall til Gsak returnerte ikke 201 CREATED");
        }

        if (gsakRespons.getBody() == null) {
            throw new KontaktskjemaException("Kall til Gsak returnerte null som body");
        }

        if (gsakRespons.getBody().getId() == null) {
            throw new KontaktskjemaException("Kall til Gsak feilet - returnert id er null");
        }
    }

    private HttpEntity<GsakInnsending> lagGsakRequestEntity(GsakInnsending gsakInnsending, String correlationId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Correlation-ID", correlationId);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(gsakInnsending, headers);
    }

    @Data
    private static class GsakInnsendingRespons {
        private Integer id;
    }
}
