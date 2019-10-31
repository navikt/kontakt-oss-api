package no.nav.tag.kontakt.oss.salesforce;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.TemaType;
import no.nav.tag.kontakt.oss.events.BesvarelseMottatt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
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
    public void besvarelseMottatt(BesvarelseMottatt event) {
        boolean suksess = event.isSuksess();
        boolean riktigTematype =
                event.getKontaktskjema().getTemaType() == TemaType.REKRUTTERING ||
                event.getKontaktskjema().getTemaType() == TemaType.REKRUTTERING_MED_TILRETTELEGGING ||
                event.getKontaktskjema().getTemaType() == TemaType.ARBEIDSTRENING;

        // TODO: Skal kun sendes til SF hvis et spesielt kontor eller fylke

        if (suksess && riktigTematype) {
            sendKontaktskjema(event.getKontaktskjema());
        }
    }

    private void sendKontaktskjema(Kontaktskjema kontaktskjema) {
        try {
            String json = new ObjectMapper().writeValueAsString(kontaktskjema);
            HttpEntity<String> request = new HttpEntity<>(json, headers());
            restTemplate.postForEntity(
                    salesforceUrl,
                    request,
                    String.class
            );
            log.info("Sendte kontaktskjema med id {} til Salesforce", kontaktskjema.getId());

        } catch (JsonProcessingException e) {
            log.error("Kunne ikke serialisere kontaktskjema", e);
        } catch (RestClientResponseException e) {
            log.error("Kunne ikke sende kontaktskjema med id {} til Salesforce", kontaktskjema.getId(), e);
        }
    }

    private HttpHeaders headers() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        // TODO: Få riktig token
        httpHeaders.setBearerAuth("");
        return httpHeaders;
    }
}
