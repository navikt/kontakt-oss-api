package no.nav.tag.kontakt.oss.featureToggles;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ByEnvironmentStrategyTest {
    @Test
    public void featureIsEnabledWhenEnvironmentInList() {
        Map<String, String> parametre = new HashMap<>();

        parametre.put("miljø", "local,dev");
        assertThat(new ByEnvironmentStrategy().isEnabledByEnvironment(parametre, "dev")).isEqualTo(true);
        assertThat(new ByEnvironmentStrategy().isEnabledByEnvironment(parametre, "local")).isEqualTo(true);
    }

    @Test
    public void featureIsDisabledWhenEnvironmentNotInList() {
        Map<String, String> parametre = new HashMap<>();

        parametre.put("miljø", "prod");
        assertThat(new ByEnvironmentStrategy().isEnabledByEnvironment(parametre, "dev")).isEqualTo(false);
    }
}
