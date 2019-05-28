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
        String miljøParameter = parameters.get("miljø");
        List<String> miljøer = List.of(miljøParameter.split(","));

        return miljøer.contains(environment);
    }
}
