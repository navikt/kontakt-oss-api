package no.nav.tag.kontakt.oss.gsak;

import no.bekk.bekkopen.org.OrganisasjonsnummerCalculator;
import no.nav.tag.kontakt.oss.DateProvider;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.KontaktskjemaException;
import no.nav.tag.kontakt.oss.TemaType;
import no.nav.tag.kontakt.oss.events.GsakOppgaveSendt;
import no.nav.tag.kontakt.oss.featureToggles.FeatureToggles;
import no.nav.tag.kontakt.oss.gsak.integrasjon.BadRequestException;
import no.nav.tag.kontakt.oss.gsak.integrasjon.GsakKlient;
import no.nav.tag.kontakt.oss.gsak.integrasjon.GsakRequest;
import no.nav.tag.kontakt.oss.navenhetsmapping.NavEnhetService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

import static no.nav.tag.kontakt.oss.TestData.kontaktskjema;
import static no.nav.tag.kontakt.oss.TestData.kontaktskjemaBuilder;
import static no.nav.tag.kontakt.oss.gsak.GsakOppgave.OppgaveStatus.DISABLED;
import static no.nav.tag.kontakt.oss.gsak.GsakOppgave.OppgaveStatus.OK;
import static no.nav.tag.kontakt.oss.gsak.GsakOppgaveService.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GsakOppgaveServiceTest {

    @Mock private GsakOppgaveRepository oppgaveRepository;
    @Mock private DateProvider dateProvider;
    @Mock private FeatureToggles featureToggles;
    @Mock private GsakKlient gsakKlient;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private NavEnhetService navEnhetService;

    private GsakOppgaveService gsakOppgaveService;

    @Captor
    ArgumentCaptor<GsakRequest> gsakRequestArgumentCaptor;

    @Before
    public void setUp() {
        when(dateProvider.now()).thenReturn(LocalDateTime.now());
        when(featureToggles.isEnabled(anyString())).thenReturn(true);

        gsakOppgaveService = new GsakOppgaveService(
                oppgaveRepository,
                dateProvider,
                gsakKlient,
                navEnhetService,
                featureToggles,
                eventPublisher
        );
    }

    @Test
    public void skalOppdatereDatabaseEtterKallTilGsak() {
        Kontaktskjema kontaktskjema = Kontaktskjema.builder().id(5).build();
        gsakOppgaveService.opprettOppgaveOgLagreStatus(kontaktskjema);

        verify(oppgaveRepository).save(eq(GsakOppgave.builder().gsakId(0).kontaktskjemaId(5).status(OK).opprettet(dateProvider.now()).build()));

    }

    @Test
    public void lagInnsendingSkalBeholdeOrgnrHvisGyldig() {
        String orgnr = OrganisasjonsnummerCalculator.getOrganisasjonsnummerList(1).get(0).getValue();
        Kontaktskjema kontaktskjema = Kontaktskjema.builder().orgnr(orgnr).build();
        GsakRequest gsakRequest = gsakOppgaveService.lagGsakInnsending(kontaktskjema);
        assertThat(gsakRequest.getOrgnr()).isEqualTo(orgnr);
    }

    @Test
    public void lagInnsendingSkalFjerneOrgnrHvisUgyldig() {
        Kontaktskjema kontaktskjema = Kontaktskjema.builder().orgnr("123").build();
        GsakRequest gsakRequest = gsakOppgaveService.lagGsakInnsending(kontaktskjema);
        assertThat(gsakRequest.getOrgnr()).isEqualTo("");
    }

    @Test
    public void skalReturnereDisabledHvisGsakToggleErAv() {
        when(featureToggles.isEnabled(eq("gsak"))).thenReturn(false);

        GsakOppgaveService gsakOppgaveService = new GsakOppgaveService(
                oppgaveRepository,
                mock(DateProvider.class),
                gsakKlient,
                mock(NavEnhetService.class),
                featureToggles,
                eventPublisher);

        gsakOppgaveService.opprettOppgaveOgLagreStatus(kontaktskjema());
        verify(oppgaveRepository).save(eq(GsakOppgave.builder().gsakId(null).status(DISABLED).build()));
    }

    @Test
    public void skalPublisereEventOmVellykketInnsending() {
        gsakOppgaveService.opprettOppgaveOgLagreStatus(kontaktskjema());
        verify(eventPublisher, times(1)).publishEvent(new GsakOppgaveSendt(true));
    }

    @Test
    public void skalPublisereEventOmFeiletInnsending() {
        when(gsakKlient.opprettGsakOppgave(any())).thenThrow(KontaktskjemaException.class);

        gsakOppgaveService.opprettOppgaveOgLagreStatus(kontaktskjema());
        verify(eventPublisher, times(1)).publishEvent(new GsakOppgaveSendt(false));
    }

    @Test
    public void opprettGsakOppgaveSkalKallesToGangerHvisReturnertBadRequest() {
        when(gsakKlient.opprettGsakOppgave(any())).thenThrow(BadRequestException.class);

        gsakOppgaveService.opprettOppgaveOgLagreStatus(kontaktskjema());
        verify(gsakKlient, times(2)).opprettGsakOppgave(any());
    }

    @Test
    public void opprettGsakOppgaveSkalKallesForAndreGangUtenOrgnr() {
        when(gsakKlient.opprettGsakOppgave(any())).thenThrow(BadRequestException.class);
        Kontaktskjema kontaktskjema = kontaktskjema();
        kontaktskjema.setOrgnr("552615255");

        gsakOppgaveService.opprettOppgaveOgLagreStatus(kontaktskjema);

        ArgumentCaptor<GsakRequest> argumentCaptor = ArgumentCaptor.forClass(GsakRequest.class);
        verify(gsakKlient, times(2)).opprettGsakOppgave(argumentCaptor.capture());
        String orgnr = argumentCaptor.getAllValues().get(1).getOrgnr();
        assertThat(orgnr).isEmpty();
    }

    @Test
    public void opprettGsakOppgaveSkalKallesMaksToGanger() {
        when(gsakKlient.opprettGsakOppgave(any()))
                .thenThrow(BadRequestException.class)
                .thenThrow(BadRequestException.class);
        try {
            gsakOppgaveService.opprettOppgaveOgLagreStatus(kontaktskjema());
        } catch (KontaktskjemaException e) {
            verify(gsakKlient, times(2)).opprettGsakOppgave(any());
        }
    }

    @Test
    public void opprettOppgaveOgLagreStatus__skal_sette_riktige_gsak_temaer_hvis_tema_er_sykefravær() {
        Kontaktskjema kontaktskjema = kontaktskjemaBuilder().temaType(TemaType.FOREBYGGE_SYKEFRAVÆR).build();

        gsakOppgaveService.opprettOppgaveOgLagreStatus(kontaktskjema);

        GsakRequest sendtRequest = capturedGsakRequest();

        assertThat(sendtRequest.getTema()).isEqualTo(GSAK_TEMA_INKLUDERENDE_ARBEIDSLIV);
        assertThat(sendtRequest.getTemagruppe()).isEqualTo(null);
    }

    @Test
    public void opprettOppgaveOgLagreStatus__skal_sette_riktige_gsak_temaer_hvis_tema_IKKE_er_sykefravær() {
        Kontaktskjema kontaktskjema = kontaktskjemaBuilder().temaType(TemaType.REKRUTTERING).build();

        gsakOppgaveService.opprettOppgaveOgLagreStatus(kontaktskjema);

        GsakRequest sendtRequest = capturedGsakRequest();

        assertThat(sendtRequest.getTema()).isEqualTo(GSAK_TEMA_OPPFØLGING_ARBEIDSGIVER);
        assertThat(sendtRequest.getTemagruppe()).isEqualTo(GSAK_TEMAGRUPPE_ARBEID);
    }

    @Test
    public void opprettOppgaveOgLagreStatus__skal_bruke_enhetsnr_til_arbeidslivssenteret_hvis_tema_er_sykefravær() {
        String enhetsnrTilArbeidslivssenter = "1234";
        when(navEnhetService.mapFraFylkesenhetNrTilArbeidslivssenterEnhetsnr(anyString())).thenReturn(enhetsnrTilArbeidslivssenter);

        Kontaktskjema kontaktskjema = kontaktskjemaBuilder().temaType(TemaType.FOREBYGGE_SYKEFRAVÆR).build();
        gsakOppgaveService.opprettOppgaveOgLagreStatus(kontaktskjema);

        GsakRequest sendtRequest = capturedGsakRequest();

        assertThat(sendtRequest.getTildeltEnhetsnr()).isEqualTo(enhetsnrTilArbeidslivssenter);
    }

    @Test
    public void opprettOppgaveOgLagreStatus__skal_bruke_enhetsnr_til_kommunalt_kontor_hvis_tema_IKKE_er_sykefravær() {
        String enhetsnrTilKommunaltKontor= "1234";
        when(navEnhetService.mapFraKommunenrTilEnhetsnr(anyString())).thenReturn(enhetsnrTilKommunaltKontor);

        Kontaktskjema kontaktskjema = kontaktskjemaBuilder().temaType(TemaType.REKRUTTERING).build();
        gsakOppgaveService.opprettOppgaveOgLagreStatus(kontaktskjema);

        GsakRequest sendtRequest = capturedGsakRequest();

        assertThat(sendtRequest.getTildeltEnhetsnr()).isEqualTo(enhetsnrTilKommunaltKontor);
    }

    private GsakRequest capturedGsakRequest() {
        verify(gsakKlient, times(1)).opprettGsakOppgave(gsakRequestArgumentCaptor.capture());
        return gsakRequestArgumentCaptor.getValue();
    }
}
