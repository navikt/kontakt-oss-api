package no.nav.tag.kontakt.oss.gsak;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.KontaktskjemaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GsakInnsending> gsakEntity = new HttpEntity<>(gsakInnsending, headers);
        ResponseEntity<GsakInnsendingRespons> respons = restTemplate.postForEntity(
                gsakUrl,
                gsakEntity,
                GsakInnsendingRespons.class
        );

        if (HttpStatus.OK.equals(respons.getStatusCode())
                && (respons.getBody() != null)
                && "OPPRETTET".equals(respons.getBody().getStatus()) // TODO Er dette riktig? TAG-233
        ) {
            return respons.getBody().getId();
        } else {
            log.info(respons.getStatusCode().toString());
            throw new KontaktskjemaException("Kall til Gsak feilet.");
        }
    }
}