package no.nav.tag.kontakt.oss.featureToggles;

import java.util.Map;

public class FeatureToggles {
    private final Map<String, Boolean> toggles;

    public FeatureToggles(Map<String, Boolean> toggles) {
        this.toggles = toggles;
    }

    public boolean isEnabled(String toggle) {
        if (!toggles.containsKey(toggle)) {
            return false;
        }
        return toggles.get(toggle);
    }
}
