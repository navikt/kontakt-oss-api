package no.nav.tag.kontakt.oss.gsak;

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

    public ResponseEntity<String> opprettGsakOppgave(GsakInnsending gsakInnsending) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Correlation-ID", UUID.randomUUID().toString());
        HttpEntity<GsakInnsending> gsakEntity = new HttpEntity<>(gsakInnsending, headers);

        ResponseEntity<String> respons = restTemplate.postForEntity(
                gsakUrl,
                gsakEntity,
                String.class
        );

        log.info("gsakstring: " + respons);

        if (HttpStatus.OK.equals(respons.getStatusCode())
                && (respons.getBody() != null)
                // && "OPPRETTET".equals(respons.getBody().getStatus()) // TODO Er dette riktig? TAG-233
        ) {
            return respons;
        } else {
            log.info(respons.getStatusCode().toString());
            throw new KontaktskjemaException("Kall til Gsak feilet.");
        }
    }
}
