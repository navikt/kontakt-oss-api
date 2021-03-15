package no.nav.arbeidsgiver.kontakt.oss.salesforce.klient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.arbeidsgiver.kontakt.oss.config.IgnoreAllErrors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static java.lang.String.format;

@Slf4j
@Component
public class SalesforceKlient {
    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;

    private final String authUrl;
    private final String apiUrl;
    private final String username;
    private final String password;
    private final String clientId;
    private final String clientSecret;

    public SalesforceKlient(
            RestTemplateBuilder restTemplateBuilder,
            ObjectMapper objectMapper,
            @Value("${salesforce.auth.url}") String authUrl,
            @Value("${salesforce.contactform.url}") String apiUrl,
            @Value("${salesforce.username}") String username,
            @Value("${salesforce.password}") String password,
            @Value("${salesforce.client.id}") String clientId,
            @Value("${salesforce.client.secret}") String clientSecret
    ) {
        this.restTemplate = restTemplateBuilder.errorHandler(new IgnoreAllErrors()).build();
        this.objectMapper = objectMapper;
        this.authUrl = authUrl;
        this.apiUrl = apiUrl;
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @SneakyThrows
    public void sendContactFormTilSalesforce(int skjemaId, ContactForm contactForm) {
        SalesforceToken token = hentSalesforceToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token.getAccessToken());

        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                new HttpEntity<>(objectMapper.writeValueAsString(contactForm), headers),
                String.class
        );

        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            log.info(
                    format("Kunne ikke sende kontaktskjema med id '%d' til Salesforce. " +
                                    "Fikk response med status: '%s' og innhold: '%s'",
                            skjemaId,
                            response.getStatusCode(),
                            response.getBody()
                    )
            );
            throw new SalesforceException("Kunne ikke sende kontaktskjema til Salesforce. Fikk status: " +
                    response.getStatusCode());
        } else {
            log.info(format(
                    "Utsending av kontaktskjema med id '%d' til Salesforce fullført med response code: '%s'",
                    skjemaId,
                    response.getStatusCode()));
        }
    }

    public SalesforceToken hentSalesforceToken() {
        String body = "grant_type=password"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&username=" + username
                + "&password=" + password;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<SalesforceToken> response = restTemplate.exchange(
                authUrl,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                SalesforceToken.class
        );

        if (HttpStatus.OK.equals(response.getStatusCode())) {
            return response.getBody();
        } else {
            throw new SalesforceException("Kunne ikke hente autorisasjonstoken til Salesforce. Fikk status: " +
                    response.getStatusCode());
        }
    }
}
