package no.nav.tag.kontakt.oss.featureToggles;

import no.finn.unleash.strategy.Strategy;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
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

    public boolean isEnabledByEnvironment(Map<String, String> parameters, String environment) {
        if (parameters == null) {
            return false;
        }

        String miljøParameter = parameters.get("miljø");
        if (miljøParameter == null) {
            return false;
        }

        List<String> miljøer = List.of(miljøParameter.split(","));
        return miljøer.contains(environment);
    }
}
