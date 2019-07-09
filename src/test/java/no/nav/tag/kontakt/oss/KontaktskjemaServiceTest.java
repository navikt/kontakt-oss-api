package no.nav.tag.kontakt.oss;

import no.nav.tag.kontakt.oss.events.BesvarelseMottatt;
import no.nav.tag.kontakt.oss.navenhetsmapping.NavEnhetService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.generate;
import static no.nav.tag.kontakt.oss.TestData.kontaktskjema;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KontaktskjemaServiceTest {

    private int maksInnsendingerPerTiMin = 10;

    @Mock
    private KontaktskjemaRepository repository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private DateProvider dateProvider;

    @Mock
    private NavEnhetService navEnhetService;

    private KontaktskjemaService kontaktskjemaService;

    @Before
    public void setUp() {
        kontaktskjemaService = new KontaktskjemaService(
                maksInnsendingerPerTiMin,
                repository,
                eventPublisher,
                dateProvider,
                navEnhetService
        );
    }

    @Test
    public void harMottattForMangeInnsendinger__skal_returnere_true_hvis_for_mange_innsendinger() {
        when(repository.findAllNewerThan(any(LocalDateTime.class)))
                .thenReturn(generate(() -> kontaktskjema()).limit(10).collect(toList()));
        assertThat(kontaktskjemaService.harMottattForMangeInnsendinger()).isTrue();
    }

    @Test
    public void harMottattForMangeInnsendinger__skal_returnere_false_hvis_ikke_for_mange_innsendinger() {
        when(repository.findAllNewerThan(any(LocalDateTime.class)))
                .thenReturn(generate(() -> kontaktskjema()).limit(9).collect(toList()));
        assertThat(kontaktskjemaService.harMottattForMangeInnsendinger()).isFalse();
    }

    @Test
    public void lagreKontaktskjema__skal_lagre_kontaktskjema_i_repo() {
        Kontaktskjema kontaktskjema = kontaktskjema();
        kontaktskjemaService.lagreKontaktskjema(kontaktskjema);
        verify(repository).save(kontaktskjema);
    }

    @Test
    public void lagreKontaktskjema__skal_sette_dato() {
        LocalDateTime now = LocalDateTime.now();
        Kontaktskjema kontaktskjema = kontaktskjema();
        when(dateProvider.now()).thenReturn(now);
        kontaktskjemaService.lagreKontaktskjema(kontaktskjema);
        assertThat(kontaktskjema.getOpprettet()).isEqualTo(now);
    }

    @Test
    public void lagreKontaktskjema__skal_sende_event_hvis_vellykket_innsending() {
        Kontaktskjema kontaktskjema = kontaktskjema();
        kontaktskjemaService.lagreKontaktskjema(kontaktskjema);
        verify(eventPublisher).publishEvent(new BesvarelseMottatt(true, kontaktskjema));
    }

    @Test
    public void lagreKontaktskjema__skal_sende_event_hvis_feilet_innsending() {
        when(repository.save(any())).thenThrow(KontaktskjemaException.class);
        Kontaktskjema kontaktskjema = kontaktskjema();

        try {
            kontaktskjemaService.lagreKontaktskjema(kontaktskjema);
        } catch (Exception ignored) {}

        verify(eventPublisher, times(1)).publishEvent(new BesvarelseMottatt(false, kontaktskjema));
    }

    @Test(expected = BadRequestException.class)
    public void lagreKontaktskjema__skal_feile_hvis_tematype_IKKE_er_forebygge_sykefravær_og_kommunenr_er_ugyldig() {
        when(navEnhetService.mapFraKommunenrTilEnhetsnr("1234")).thenThrow(KontaktskjemaException.class);

        Kontaktskjema kontaktskjema = kontaktskjema();
        kontaktskjema.setTemaType(TemaType.REKRUTTERING);
        kontaktskjema.setKommunenr("1234");

        kontaktskjemaService.lagreKontaktskjema(kontaktskjema);
    }

    @Test
    public void lagreKontaktskjema__skal_fungere_hvis_tematype_IKKE_er_forebygge_sykefravær_og_kommunenr_er_gyldig() {
        when(navEnhetService.mapFraKommunenrTilEnhetsnr("1234")).thenReturn("4321");

        Kontaktskjema kontaktskjema = kontaktskjema();
        kontaktskjema.setTemaType(TemaType.REKRUTTERING);
        kontaktskjema.setKommunenr("1234");

        kontaktskjemaService.lagreKontaktskjema(kontaktskjema);
    }

    @Test(expected = BadRequestException.class)
    public void lagreKontaktskjema__skal_feile_hvis_tematype_er_forebygge_sykefravær_og_fylke_er_ugyldig() {
        when(navEnhetService.mapFraFylkesenhetNrTilArbeidslivssenterEnhetsnr("1234")).thenThrow(KontaktskjemaException.class);

        Kontaktskjema kontaktskjema = kontaktskjema();
        kontaktskjema.setTemaType(TemaType.FOREBYGGE_SYKEFRAVÆR);
        kontaktskjema.setFylke("1234");

        kontaktskjemaService.lagreKontaktskjema(kontaktskjema);
    }

    @Test
    public void lagreKontaktskjema__skal_fungere_hvis_tematype_er_forebygge_sykefravær_og_fylke_er_gyldig() {
        when(navEnhetService.mapFraFylkesenhetNrTilArbeidslivssenterEnhetsnr("1234")).thenReturn("4321");

        Kontaktskjema kontaktskjema = kontaktskjema();
        kontaktskjema.setTemaType(TemaType.FOREBYGGE_SYKEFRAVÆR);
        kontaktskjema.setFylke("1234");

        kontaktskjemaService.lagreKontaktskjema(kontaktskjema);
    }
}
