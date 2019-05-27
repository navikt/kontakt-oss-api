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

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FeatureToggleControllerTest {

    @Mock HttpServletResponse response;

    private FakeUnleash fakeUnleash = new FakeUnleash();
    private FeatureToggleController featureToggleController = new FeatureToggleController(fakeUnleash);

    @Before
    public void setup() {
        fakeUnleash.enable("darkMode");
        fakeUnleash.disable("lightMode");
    }

    @Test
    public void skalSetteCookieHvisIngenCookie() {
        featureToggleController.feature("darkMode", null, response);
        verify(response).addCookie(any());
    }

    @Test
    public void skalIkkeSetteCookieHvisManHarCookie() {
        featureToggleController.feature("darkMode", "blabla", response);
        verify(response, times(0)).addCookie(any());
    }

    @Test
    public void skalReturnereStatus200VedGet() {
        assertThat(featureToggleController.feature("darkMode", null, response).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(featureToggleController.feature("nightMode", null, response).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

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
}
