package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NorgServiceTest {

    @Mock
    private NorgKlient norgKlient;

    private NorgService norgService;

    @Before
    public void setUp() {
        norgService = new NorgService(norgKlient);
    }

    @Test
    public void geografi__skal_ikke_ta_med_kommuner_med_null() {
        mockResponsFraNorgKlient(
                new NorgGeografi("01", "et fylke"),
                new NorgGeografi("0101", null),
                new NorgGeografi(null, "blabla"),
                new NorgGeografi(null, null)
        );
        List<KommuneEllerBydel> kommunerOgBydeler = norgService.hentListeOverAlleKommunerOgBydeler();
        assertThat(kommunerOgBydeler).isEmpty();
    }

    @Test
    public void geografi__skal_ikke_ta_med_norgGeografier_med_bokstaver_i_navn() {
        mockResponsFraNorgKlient(
                new NorgGeografi("01", "et fylke"),
                new NorgGeografi("0101a", "kommune")
        );
        List<KommuneEllerBydel> kommunerOgBydeler = norgService.hentListeOverAlleKommunerOgBydeler();
        assertThat(kommunerOgBydeler).isEmpty();
    }

    @Test
    public void geografi__skal_ikke_ta_med_norgGeografier_med_navn_av_ugyldig_lengde() {
        mockResponsFraNorgKlient(
                new NorgGeografi("01", "et fylke"),
                new NorgGeografi("010", "kommune")
        );
        List<KommuneEllerBydel> kommunerOgBydeler = norgService.hentListeOverAlleKommunerOgBydeler();
        assertThat(kommunerOgBydeler).isEmpty();
    }

    @Test
    public void geografi__skal_ikke_legge_til_kommunen_hvis_den_har_bydeler() {
        mockResponsFraNorgKlient(
                new NorgGeografi("01", "fylke"),
                new NorgGeografi("0101", "kommune"),
                new NorgGeografi("010101", "bydel")
        );
        List<KommuneEllerBydel> kommunerOgBydeler = norgService.hentListeOverAlleKommunerOgBydeler();
        assertThat(kommunerOgBydeler).doesNotContain(new Kommune("0101", "kommune"));
    }

    @Test
    public void geografi__skal_legge_til_bydeler_med_kommunenavn_lagt_paa() {
        mockResponsFraNorgKlient(
                new NorgGeografi("01", "fylke"),
                new NorgGeografi("0101", "kommune"),
                new NorgGeografi("010101", "bydel1"),
                new NorgGeografi("010102", "bydel2")
        );
        List<KommuneEllerBydel> kommunerTilhoerendeFylke = norgService.hentListeOverAlleKommunerOgBydeler();
        assertThat(kommunerTilhoerendeFylke).contains(
                new Bydel("010101", "kommune–bydel1"),
                new Bydel("010102", "kommune–bydel2")
        );
    }

    private void mockResponsFraNorgKlient(NorgGeografi ... norgGeografi) {
        when(norgKlient.hentGeografiFraNorg()).thenReturn(Arrays.asList(norgGeografi));
    }
}