package no.nav.arbeidsgiver.kontakt.oss.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Bean(name = "restTemplate")
    public RestTemplate restTemplate(
            RestTemplateBuilder restTemplateBuilder
    ) {
        return restTemplateBuilder.build();
    }
}
