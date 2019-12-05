package no.nav.tag.kontakt.oss.salesforce;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
    }

    public String getBearerAuth() {
        String body = "grant_type=password"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&username=" + username
                + "&password=" + password;

        ResponseEntity<String> response = restTemplate.exchange(
                authUrl,
                HttpMethod.GET,
                new HttpEntity<>(body),
                String.class
        );

        return response.getBody();
    }

}
