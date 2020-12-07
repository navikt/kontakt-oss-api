package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter;

import no.nav.arbeidsgiver.kontakt.oss.KontaktskjemaException;
import no.nav.arbeidsgiver.kontakt.oss.events.FylkesinndelingOppdatert;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.KodeverkKlient;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgKlient;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgOrganisering;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FylkesinndelingServiceTest {

    @Mock
    private NorgKlient norgKlient;

    @Mock
    private KodeverkKlient kodeverkKlient;

    @Mock
    private FylkesinndelingRepository fylkesinndelingRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private FylkesinndelingService fylkesinndelingService;

    @BeforeEach
    public void setUp() {
        fylkesinndelingService = new FylkesinndelingService(norgKlient, kodeverkKlient, fylkesinndelingRepository, eventPublisher);
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
        Assertions.assertThat(kommunerOgBydeler).doesNotContain(new Kommune("0101", "kommune"));
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
        Assertions.assertThat(kommunerTilhoerendeFylke).contains(
                new Bydel("010101", "kommune - bydel1"),
                new Bydel("010102", "kommune - bydel2")
        );
    }

    @Test
    public void hentMapFraNavenhetOgFylkesenhet__skal_returnere_map_med_data() {
        when(norgKlient.hentOrganiseringFraNorg()).thenReturn(Arrays.asList(
                new NorgOrganisering("1111", "Aktiv", "1400"),
                new NorgOrganisering("2222", "Aktiv", "1500")
        ));
        assertThat(fylkesinndelingService.hentMapFraNavenhetTilFylkesenhet()).isEqualTo(new HashMap<>() {{
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

    @Test
    public void oppdaterFylkesinndeling__skal_publisere_fylkesinndeling_oppdatert_hvis_ok() {
        fylkesinndelingService.oppdaterFylkesinndeling();
        verify(eventPublisher, times(1)).publishEvent(new FylkesinndelingOppdatert(true));
    }

    @Test
    public void oppdaterFylkesinndeling__skal_publisere_fylkesinndeling_oppdatert_feil_hvis_feil() {
        when(fylkesinndelingService.hentListeOverAlleKommunerOgBydeler()).thenThrow(KontaktskjemaException.class);
        try {
            fylkesinndelingService.oppdaterFylkesinndeling();
        } catch (KontaktskjemaException exception) {
            verify(eventPublisher, times(1)).publishEvent(new FylkesinndelingOppdatert(false));
        }
    }

    @Test
    public void oppdaterFylkesinndeling__skal_kaste_kontaktskjema_exception_videre() {
        when(fylkesinndelingService.hentListeOverAlleKommunerOgBydeler()).thenThrow(KontaktskjemaException.class);
        assertThrows(KontaktskjemaException.class, () ->fylkesinndelingService.oppdaterFylkesinndeling());
    }

}
