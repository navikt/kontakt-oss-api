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
        System.out.println("Parameters: " + parameters);

        if (parameters == null) {
            return false;
        }

        String miljøParameter = parameters.get("miljø");
        System.out.println("MiljøParameter: " + miljøParameter);
        if (miljøParameter == null) {
            return false;
        }

        String[] miljøer = miljøParameter.split(",");
        System.out.println("Miljøer: " + miljøer);
        System.out.println("Feature: " + Arrays.asList(miljøer).contains(environment));

        return Arrays.asList(miljøer).contains(environment);
    }
}
