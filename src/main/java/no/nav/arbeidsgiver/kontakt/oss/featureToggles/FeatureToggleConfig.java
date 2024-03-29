package no.nav.arbeidsgiver.kontakt.oss.featureToggles;

import no.finn.unleash.DefaultUnleash;
import no.finn.unleash.FakeUnleash;
import no.finn.unleash.Unleash;
import no.finn.unleash.strategy.GradualRolloutSessionIdStrategy;
import no.finn.unleash.util.UnleashConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
public class FeatureToggleConfig {

    private final String APP_NAME = "kontakt-oss-api";
    private final ByClusterStrategy byClusterStrategy;

    @Value("${unleash.url}")
    private String unleashUrl;
    @Value("${unleash.profile}")
    private String profile;

    @Autowired
    public FeatureToggleConfig(ByClusterStrategy byClusterStrategy) {
        this.byClusterStrategy = byClusterStrategy;
    }


    @Bean
    @Profile(value = {"dev", "prod"})
    public Unleash initializeUnleash() {
        UnleashConfig config = UnleashConfig.builder()
                .appName(APP_NAME)
                .instanceId(APP_NAME + "-" + profile)
                .unleashAPI(unleashUrl)
                .build();

        return new DefaultUnleash(
                config,
                byClusterStrategy,
                new GradualRolloutSessionIdStrategy()
        );
    }

    @Bean
    @Profile(value = {"local"})
    public Unleash unleashMock() {
        FakeUnleash fakeUnleash = new FakeUnleash();
        fakeUnleash.enableAll();
        return fakeUnleash;
    }
}
