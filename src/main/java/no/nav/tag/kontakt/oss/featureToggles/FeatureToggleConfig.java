package no.nav.tag.kontakt.oss.featureToggles;

import no.finn.unleash.DefaultUnleash;
import no.finn.unleash.util.UnleashConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class FeatureToggleConfig {

    @Value("${ENABLE_GSAK:false}") private String gsak;
    @Value("${unleash.url}") private String unleashUrl;

    @Bean
    public FeatureToggles featureToggles() {
        return new FeatureToggles(Collections.singletonMap("gsak", "true".equals(gsak)));
    }

    @Bean("unleash")
    public DefaultUnleash initializeUnleash() {
        UnleashConfig config = UnleashConfig.builder()
                .appName("tag-initializeUnleash")
                .instanceId("min-instanse")
                .unleashAPI(unleashUrl)
                .build();

        return new DefaultUnleash(config);
    }
}
