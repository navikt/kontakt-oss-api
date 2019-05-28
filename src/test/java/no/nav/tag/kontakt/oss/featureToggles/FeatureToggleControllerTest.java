package no.nav.tag.kontakt.oss.featureToggles;

import no.finn.unleash.FakeUnleash;
import no.finn.unleash.Unleash;
import org.eclipse.jetty.security.authentication.DeferredAuthentication;
import org.eclipse.jetty.server.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FeatureToggleControllerTest {

    @Mock HttpServletResponse response;
    @Mock FeatureToggleService featureToggleService;

    private FeatureToggleController featureToggleController;

    @Before
    public void setup() {
        featureToggleController = new FeatureToggleController(featureToggleService);
    }

    @Test
    public void feature__skal_sette_cookie_hvis_ingen_cookie() {
        featureToggleController.feature(null, null, response);
        verify(response).addCookie(any());
    }

    @Test
    public void feature__skal_ikke_sette_cookie_hvis_man_har_cookie() {
        featureToggleController.feature(null, "blabla", response);
        verify(response, times(0)).addCookie(any());
    }

    @Test
    public void feature__skal_returnere_status_200_ved_get() {
        assertThat(featureToggleController.feature(Arrays.asList("darkMode", "nightMode"), null, response).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void feature__skal_returnere_respons_fra_service() {
        List<String> features = Arrays.asList("darkMode", "nightMode");
        Map<String, Boolean> toggles = new HashMap<>(){{
            put("darkMode", true);
            put("nightMode", false);
        }};

        when(featureToggleService.hentFeatureToggles(eq(features), any())).thenReturn(toggles);

        Map<String, Boolean> resultat = featureToggleController.feature(features, null, response).getBody();

        assertThat(resultat).isEqualTo(toggles);
    }

    /* TODO Flytt disse testene til service
    @Test
    public void skalReturnereTrueHvisFeatureErPå() {
        assertThat(featureToggleController.feature("darkMode", null, response).getBody()).isEqualTo(true);
    }

    @Test
    public void skalReturnereFalseHvisFeatureErAv() {
        assertThat(featureToggleController.feature("lightMode", null, response).getBody()).isEqualTo(false);
    }

    @Test
    public void skalReturnereFalseDersomFeatureIkkeFinnes() {
        assertThat(featureToggleController.feature("nightMode", null, response).getBody()).isEqualTo(false);
    }
     */
}
