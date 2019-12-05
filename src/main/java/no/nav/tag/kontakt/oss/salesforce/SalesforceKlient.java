package no.nav.tag.kontakt.oss.salesforce;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Component
public class SalesforceKlient {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final RestTemplate restTemplate;

    private final String authUrl;
    private final String apiUrl;
    private final String username;
    private final String password;
    private final String clientId;
    private final String clientSecret;

    public SalesforceKlient(
            RestTemplate restTemplate,
            @Value("${salesforce.auth.url}") String authUrl,
            @Value("${salesforce.contactform.url}") String apiUrl,
            @Value("${salesforce.username}") String username,
            @Value("${salesforce.password}") String password,
            @Value("${salesforce.client.id}") String clientId,
            @Value("${salesforce.client.secret}") String clientSecret
    ) {
        this.restTemplate = restTemplate;
        this.authUrl = authUrl;
        this.apiUrl = apiUrl;
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @SneakyThrows
    public void sendKontaktskjemaTilSalesforce(Kontaktskjema kontaktskjema) {
        ContactForm contactForm = new ContactForm(
                kontaktskjema.getTema(),
                kontaktskjema.getKommunenr(),
                kontaktskjema.getBedriftsnavn(),
                kontaktskjema.getOrgnr(),
                kontaktskjema.getFornavn(),
                kontaktskjema.getEtternavn(),
                kontaktskjema.getEpost(),
                kontaktskjema.getTelefonnr()
        );

        SalesforceToken token = hentSalesforceToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token.getAccessToken());

        try {
            restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(objectMapper.writeValueAsString(contactForm), headers),
                    String.class
            );
        } catch (RestClientResponseException e) {
            throw new SalesforceException("Kunne ikke sende kontaktskjema til Salesforce", e);
        }
    }

    private SalesforceToken hentSalesforceToken() {
        String body = "grant_type=password"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&username=" + username
                + "&password=" + password;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            ResponseEntity<SalesforceToken> response = restTemplate.exchange(
                    authUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    SalesforceToken.class
            );

            return response.getBody();
        } catch (RestClientResponseException e) {
            throw new SalesforceException("Kunne ikke hente autorisasjonstoken til Salesforce", e);
        }

    }

}
