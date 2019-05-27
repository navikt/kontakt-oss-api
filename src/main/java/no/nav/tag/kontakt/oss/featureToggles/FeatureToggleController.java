package no.nav.tag.kontakt.oss.featureToggles;

import no.finn.unleash.Unleash;
import no.finn.unleash.UnleashContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
public class FeatureToggleController {
    private final Unleash unleash;
    private final String UNLEASH_SESSION = "unleash-session";

    @Autowired
    public FeatureToggleController(Unleash unleash) {
        this.unleash = unleash;
    }

    @GetMapping("/feature/{feature}")
    public ResponseEntity feature(
            @PathVariable String feature,
            @CookieValue(name = UNLEASH_SESSION, required = false) String unleashSession,
            HttpServletResponse response
    ) {
        String sessionId = unleashSession;

        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            response.addCookie(new Cookie(UNLEASH_SESSION, sessionId));
        }

        UnleashContext unleashContext = UnleashContext.builder().sessionId(sessionId).build();
        boolean isEnabled = unleash.isEnabled(feature, unleashContext);

        return ResponseEntity.status(HttpStatus.OK).body(isEnabled);
    }
}
