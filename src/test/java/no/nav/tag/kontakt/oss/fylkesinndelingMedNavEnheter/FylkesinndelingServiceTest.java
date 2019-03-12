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
public class FylkesinndelingServiceTest {

    @Mock
    private NorgKlient norgKlient;

    @Mock
    private KodeverkKlient kodeverkKlient;

    private FylkesinndelingService fylkesinndelingService;

    @Before
    public void setUp() {
        fylkesinndelingService = new FylkesinndelingService(norgKlient, kodeverkKlient);
    }

    @Test
    public void hentListeOverAlleKommunerOgBydeler__skal_ikke_legge_til_kommunen_hvis_den_har_bydeler() {
        when(kodeverkKlient.hentKommuner()).thenReturn(Arrays.asList(
                new Kommune("0101", "kommune")
        ));
        when(kodeverkKlient.hentBydeler()).thenReturn(Arrays.asList(
                new Bydel("010101", "bydel")
        ));
        List<KommuneEllerBydel> kommunerOgBydeler = fylkesinndelingService.hentListeOverAlleKommunerOgBydeler();
        assertThat(kommunerOgBydeler).doesNotContain(new Kommune("0101", "kommune"));
    }

    @Test
    public void hentListeOverAlleKommunerOgBydeler__skal_legge_til_bydeler_med_kommunenavn_lagt_paa() {
        when(kodeverkKlient.hentKommuner()).thenReturn(Arrays.asList(
                new Kommune("0101", "kommune")
        ));

        when(kodeverkKlient.hentBydeler()).thenReturn(Arrays.asList(
                new Bydel("010101", "bydel1"),
                new Bydel("010102", "bydel2")
        ));

        List<KommuneEllerBydel> kommunerTilhoerendeFylke = fylkesinndelingService.hentListeOverAlleKommunerOgBydeler();
        assertThat(kommunerTilhoerendeFylke).contains(
                new Bydel("010101", "kommune–bydel1"),
                new Bydel("010102", "kommune–bydel2")
        );
    }

    @Test
    public void hentMapFraNavenhetOgFylkesenhet__skal_returnere_map_med_data() {
        when(norgKlient.hentOrganiseringFraNorg()).thenReturn(Arrays.asList(
                new NorgOrganisering("1111", "Aktiv", "1400"),
                new NorgOrganisering("2222", "Aktiv", "1500")
        ));
        assertThat(fylkesinndelingService.hentMapFraNavenhetTilFylkesenhet()).isEqualTo(new HashMap<>(){{
            put(new NavEnhet("1111"), new NavFylkesenhet("1400"));
            put(new NavEnhet("2222"), new NavFylkesenhet("1500"));
        }});
    }

    @Test
    public void hentMapFraNavenhetOgFylkesenhet__skal_ikke_ta_med_fylkesenheter_som_er_null() {
        when(norgKlient.hentOrganiseringFraNorg()).thenReturn(Collections.singletonList(
                new NorgOrganisering("1111", "Aktiv", null)
        ));
        assertThat(fylkesinndelingService.hentMapFraNavenhetTilFylkesenhet()).isEmpty();
    }

    @Test
    public void hentMapFraNavenhetOgFylkesenhet__skal_ikke_ta_med_navEnheter_som_ikke_er_aktive() {
        when(norgKlient.hentOrganiseringFraNorg()).thenReturn(Arrays.asList(
                new NorgOrganisering("1111", "Nedlagt", "1400"),
                new NorgOrganisering("2222", "Blabla", "1500")
        ));
        assertThat(fylkesinndelingService.hentMapFraNavenhetTilFylkesenhet().keySet()).doesNotContain(
                new NavEnhet("1111"),
                new NavEnhet("2222")
        );
    }

}