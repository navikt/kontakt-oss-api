package no.nav.tag.kontakt.oss.featureToggles;

import no.finn.unleash.DefaultUnleash;
import no.finn.unleash.Unleash;
import no.finn.unleash.util.UnleashConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class FeatureToggleConfig {

    private final String APP_NAME = "kontakt-oss-api";
    @Value("${ENABLE_GSAK:false}") private String gsak;
    @Value("${unleash.url}") private String unleashUrl;
    @Value("${spring.profiles}") private String profile;

    @Bean
    public FeatureToggles featureToggles() {
        return new FeatureToggles(Collections.singletonMap("gsak", "true".equals(gsak)));
    }

    @Bean("unleash")
    public Unleash initializeUnleash() {
        UnleashConfig config = UnleashConfig.builder()
                .appName(APP_NAME)
                .instanceId(APP_NAME + "-" + profile)
                .unleashAPI(unleashUrl)
                .build();

        return new DefaultUnleash(config);
    }
}
