package no.nav.tag.kontakt.oss.featureToggles;

import no.finn.unleash.FakeUnleash;
import no.finn.unleash.Unleash;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class FeatureToggleControllerTest {

    @Mock HttpServletResponse request;

    private FakeUnleash fakeUnleash = new FakeUnleash();
    private FeatureToggleController featureToggleController = new FeatureToggleController(fakeUnleash);

    @Before
    public void setup() {
        fakeUnleash.enable("darkMode");
        fakeUnleash.disable("lightMode");
    }

    @Test
    public void skalReturnereStatus200VedGet() {
        assertThat(featureToggleController.feature("darkMode", null, request).getStatusCode(), is(HttpStatus.OK));
        assertThat(featureToggleController.feature("nightMode", null, request).getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void skalReturnereTrueHvisFeatureErPå() {
        assertThat(featureToggleController.feature("darkMode", null, request).getBody(), is(true));
    }

    @Test
    public void skalReturnereFalseHvisFeatureErAv() {
        assertThat(featureToggleController.feature("lightMode", null, request).getBody(), is(false));
    }

    @Test
    public void skalReturnereFalseDersomFeatureIkkeFinnes() {
        assertThat(featureToggleController.feature("nightMode", null, request).getBody(), is(false));
    }
}
