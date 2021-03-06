package no.nav.arbeidsgiver.kontakt.oss.featureToggles;

import no.finn.unleash.Unleash;
import no.finn.unleash.UnleashContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeatureToggleServiceTest {

    @Mock
    private Unleash unleash;

    private FeatureToggleService featureToggleService;

    @BeforeEach
    public void setUp() {
        featureToggleService = new FeatureToggleService(unleash);
    }

    @Test
    public void hentFeatureToggles__skal_returnere_true_hvis_feature_er_på() {
        when(unleash.isEnabled(eq("feature_som_er_på"), any(UnleashContext.class))).thenReturn(true);
        Map<String, Boolean> toggles = featureToggleService.hentFeatureToggles(Arrays.asList("feature_som_er_på"), null);
        assertThat(toggles.get("feature_som_er_på")).isTrue();
    }

    @Test
    public void hentFeatureToggles__skal_returnere_false_hvis_feature_er_av() {
        when(unleash.isEnabled(eq("feature_som_er_av"), any(UnleashContext.class))).thenReturn(false);
        Map<String, Boolean> toggles = featureToggleService.hentFeatureToggles(Arrays.asList("feature_som_er_av"), null);
        assertThat(toggles.get("feature_som_er_av")).isFalse();
    }

    @Test
    public void hentFeatureToggles__skal_returnere_false_hvis_feature_ikke_finnes() {
        Map<String, Boolean> toggles = featureToggleService.hentFeatureToggles(Arrays.asList("feature_som_ikke_finnes"), null);
        assertThat(toggles.get("feature_som_ikke_finnes")).isFalse();
    }

    @Test
    public void hentFeatureToggles__skal_kunne_returnere_flere_toggles() {
        List<String> features = Arrays.asList("feature1", "feature2", "feature3");
        when(unleash.isEnabled(eq("feature1"), any(UnleashContext.class))).thenReturn(true);
        when(unleash.isEnabled(eq("feature2"), any(UnleashContext.class))).thenReturn(false);

        Map<String, Boolean> toggles = featureToggleService.hentFeatureToggles(features, null);

        assertThat(toggles.get("feature1")).isTrue();
        assertThat(toggles.get("feature2")).isFalse();
        assertThat(toggles.get("feature3")).isFalse();
        assertThat(toggles.size()).isEqualTo(3);
    }
}
