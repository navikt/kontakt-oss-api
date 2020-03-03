package no.nav.arbeidsgiver.kontakt.oss.gsak.integrasjon;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

    @Value("${GSAK_BRUKERNAVN:default}")
    private String brukernavn;

    @Value("${GSAK_PASSORD:default}")
    private String passord;

    @Bean(name = "gsakRestTemplate")
    public RestTemplate gsakRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(brukernavn, passord));
        return restTemplate;
    }

}
