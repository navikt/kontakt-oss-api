package no.nav.tag.kontakt.oss.featureToggles;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class FeatureToggleConfig {

    @Value("${ENABLE_GSAK:false}")
    private boolean gsak;

    @Bean
    public FeatureToggles featureToggles() {
        return new FeatureToggles(Collections.singletonMap("gsak", gsak));
    }
}
