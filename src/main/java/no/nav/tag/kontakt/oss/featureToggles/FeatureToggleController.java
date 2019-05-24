package no.nav.tag.kontakt.oss.featureToggles;

import no.finn.unleash.Unleash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeatureToggleController {
    private final Unleash unleash;

    @Autowired
    public FeatureToggleController(Unleash unleash) {
        this.unleash = unleash;
    }

    @GetMapping("/feature/{feature}")
    public ResponseEntity feature(@PathVariable String feature) {
        boolean isEnabled = unleash.isEnabled(feature);
        return ResponseEntity.status(HttpStatus.OK).body(isEnabled);
    }
}
