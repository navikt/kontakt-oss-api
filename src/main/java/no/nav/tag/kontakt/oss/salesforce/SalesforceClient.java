package no.nav.tag.kontakt.oss.salesforce;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.events.GsakOppgaveOpprettet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class SalesforceClient {

    private final RestTemplate restTemplate;
    private final String salesforceUrl;


    public SalesforceClient(
            RestTemplate restTemplate,
            @Value("${salesforce.url}") String salesforceUrl
    ) {
        this.restTemplate = restTemplate;
        this.salesforceUrl = salesforceUrl;
    }

    @EventListener
    public void besvarelseMottatt(GsakOppgaveOpprettet event) {
        sendKontaktskjema(event.getKontaktskjema());
    }

    private void sendKontaktskjema(Kontaktskjema kontaktskjema) {
        HttpHeaders headers = getHeaders();

        JSONObject json = new JSONObject();
        json.put("kontaktskjema", kontaktskjema);
        HttpEntity<String> request = new HttpEntity<>(json.toString(), headers);

        try {
            ResponseEntity<String> respons = restTemplate.postForEntity(
                    salesforceUrl,
                    request,
                    String.class
            );
        } catch (RestClientException e) {
            log.error("Kunne ikke sende kontaktskjema med id {} til Salesforce", kontaktskjema.getId())
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return httpHeaders;
    }
}
