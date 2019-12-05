package no.nav.tag.kontakt.oss.salesforce;

import no.nav.tag.kontakt.oss.Kontaktskjema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SalesforceKlient {
    private final String authUrl = "https://test.salesforce.com/services/oauth2/token";

    private final RestTemplate restTemplate;

    private final String username;
    private final String password;
    private final String clientId;
    private final String clientSecret;

    public SalesforceKlient(
            RestTemplate restTemplate,
            @Value("${salesforce.username}") String username,
            @Value("${salesforce.password}") String password,
            @Value("${salesforce.client.id}") String clientId,
            @Value("${salesforce.client.secret}") String clientSecret
    ) {
        this.restTemplate = restTemplate;

        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.clientSecret = clientSecret;

        hentSalesforceToken();
    }

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


    }

    private SalesforceToken hentSalesforceToken() {
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

        return response.getBody();
    }

}
