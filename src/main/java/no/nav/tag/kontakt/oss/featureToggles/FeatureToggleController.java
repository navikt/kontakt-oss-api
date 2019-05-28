package no.nav.tag.kontakt.oss.featureToggles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class FeatureToggleController {
    private final FeatureToggleService featureToggleService;
    private final String UNLEASH_SESSION = "unleash-session";

    @Autowired
    public FeatureToggleController(FeatureToggleService featureToggleService) {
        this.featureToggleService = featureToggleService;
    }

    @GetMapping("/feature")
    public ResponseEntity<Map<String, Boolean>> feature(
            @RequestParam("feature") List<String> features,
            @CookieValue(name = UNLEASH_SESSION, required = false) String unleashSession,
            HttpServletResponse response
    ) {
        String sessionId = unleashSession;

        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            response.addCookie(new Cookie(UNLEASH_SESSION, sessionId));
        }

        Map<String, Boolean> toggles = featureToggleService.hentFeatureToggles(features, sessionId);
        return ResponseEntity.status(HttpStatus.OK).body(toggles);

    }
}
