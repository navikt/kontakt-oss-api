package no.nav.tag.kontakt.oss.gsak;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

    @Value("${GSAK_BRUKERNAVN:}")
    private String brukernavn;

    @Value("${GSAK_PASSORD:}")
    private String passord;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(brukernavn, passord));
        return restTemplate;
    }

}
