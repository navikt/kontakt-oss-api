package no.nav.tag.kontakt.oss.featureToggles;

import no.finn.unleash.Unleash;
import no.finn.unleash.UnleashContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeatureToggleService {
    private final Unleash unleash;

    public FeatureToggleService(Unleash unleash) {
        this.unleash = unleash;
    }


    public Map<String, Boolean> hentFeatureToggles(List<String> features, String sessionId) {
        UnleashContext unleashContext = UnleashContext.builder().sessionId(sessionId).build();

        Map<String, Boolean> toggles = new HashMap<>();
        features.forEach(feature -> toggles.put(feature, unleash.isEnabled(feature, unleashContext)));

        return toggles;
    }
}
