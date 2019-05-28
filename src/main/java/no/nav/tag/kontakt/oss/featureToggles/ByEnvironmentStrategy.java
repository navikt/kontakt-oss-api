package no.nav.tag.kontakt.oss.featureToggles;

import no.finn.unleash.strategy.Strategy;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.Map;

public class ByEnvironmentStrategy implements Strategy {
    @Value("${spring.profiles") String environment;

    @Override
    public String getName() {
        return "byEnvironment";
    }

    @Override
    public boolean isEnabled(Map<String, String> parameters) {
        return isEnabledByEnvironment(parameters, environment);
    }

    boolean isEnabledByEnvironment(Map<String, String> parameters, String environment) {
        if (parameters == null) {
            return false;
        }

        String miljøParameter = parameters.get("miljø");
        if (miljøParameter == null) {
            return false;
        }

        String[] miljøer = miljøParameter.split(",");
        return Arrays.asList(miljøer).contains(environment);
    }
}
