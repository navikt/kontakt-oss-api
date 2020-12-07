package no.nav.arbeidsgiver.kontakt.oss.gsak;

import no.bekk.bekkopen.org.OrganisasjonsnummerCalculator;
import no.nav.arbeidsgiver.kontakt.oss.*;
import no.nav.arbeidsgiver.kontakt.oss.events.GsakOppgaveOpprettet;
import no.nav.arbeidsgiver.kontakt.oss.events.GsakOppgaveSendt;
import no.nav.arbeidsgiver.kontakt.oss.gsak.integrasjon.GsakKlient;
import no.nav.arbeidsgiver.kontakt.oss.gsak.integrasjon.GsakRequest;
import no.nav.arbeidsgiver.kontakt.oss.navenhetsmapping.NavEnhetService;
import no.nav.arbeidsgiver.kontakt.oss.testUtils.TestData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

import static no.nav.arbeidsgiver.kontakt.oss.gsak.GsakOppgaveService.FYLKESENHETNR_TIL_MØRE_OG_ROMSDAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GsakOppgaveServiceTest {

    @Mock
    private GsakOppgaveRepository oppgaveRepository;
    @Mock
    private DateProvider dateProvider;
    @Mock
    private GsakKlient gsakKlient;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private NavEnhetService navEnhetService;

    private GsakOppgaveService gsakOppgaveService;

    @Captor
    ArgumentCaptor<GsakRequest> gsakRequestArgumentCaptor;

    @BeforeEach
    public void setUp() {
        when(dateProvider.now()).thenReturn(LocalDateTime.now());

        gsakOppgaveService = new GsakOppgaveService(
                oppgaveRepository,
                dateProvider,
                gsakKlient,
                navEnhetService,
                eventPublisher
        );
    }

    @Test
    public void skalOppdatereDatabaseEtterKallTilGsak() {
        Kontaktskjema kontaktskjema = Kontaktskjema.builder().fylkesenhetsnr(FYLKESENHETNR_TIL_MØRE_OG_ROMSDAL).id(5).build();
        gsakOppgaveService.opprettOppgaveOgLagreStatus(kontaktskjema);

        verify(oppgaveRepository).save(eq(GsakOppgave.builder().gsakId(0).kontaktskjemaId(5).status(GsakOppgave.OppgaveStatus.OK).opprettet(dateProvider.now()).build()));

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
    public void skalPublisereGsakOppgaveSendtOmVellykketInnsending() {
        gsakOppgaveService.opprettOppgaveOgLagreStatus(TestData.kontaktskjema());

        ArgumentCaptor<GsakOppgaveSendt> argumentCaptor = forClass(GsakOppgaveSendt.class);
        //Det publiseres to eventer med ulike argumenter, derfor times(2) selv om vi bare er interessert i den siste.
        verify(eventPublisher, times(2)).publishEvent(argumentCaptor.capture());
        Assertions.assertThat(argumentCaptor.getValue().getBehandlingsresultat().getStatus()).isEqualTo(GsakOppgave.OppgaveStatus.OK);
    }

    @Test
    public void skalPublisereGsakOppgaveOpprettetOmVelykketInnsending() {
        Kontaktskjema kontaktskjema = TestData.kontaktskjema();
        Integer gsakId = 1;
        when(gsakKlient.opprettGsakOppgave(any())).thenReturn(gsakId);
        gsakOppgaveService.opprettOppgaveOgLagreStatus(kontaktskjema);

        verify(eventPublisher, times(1)).publishEvent(new GsakOppgaveOpprettet(gsakId, kontaktskjema));
    }

    @Test
    public void skalPublisereEventOmFeiletInnsending() {
        when(gsakKlient.opprettGsakOppgave(any())).thenThrow(KontaktskjemaException.class);

        gsakOppgaveService.opprettOppgaveOgLagreStatus(TestData.kontaktskjema());

        ArgumentCaptor<GsakOppgaveSendt> argumentCaptor = forClass(GsakOppgaveSendt.class);
        verify(eventPublisher, times(1)).publishEvent(argumentCaptor.capture());
        Assertions.assertThat(argumentCaptor.getValue().getBehandlingsresultat().getStatus()).isEqualTo(GsakOppgave.OppgaveStatus.FEILET);
    }

    @Test
    public void opprettGsakOppgaveSkalKallesToGangerHvisReturnertBadRequest() {
        when(gsakKlient.opprettGsakOppgave(any())).thenThrow(BadRequestException.class);

        gsakOppgaveService.opprettOppgaveOgLagreStatus(TestData.kontaktskjema());
        verify(gsakKlient, times(2)).opprettGsakOppgave(any());
    }

    @Test
    public void opprettGsakOppgaveSkalKallesForAndreGangUtenOrgnr() {
        when(gsakKlient.opprettGsakOppgave(any())).thenThrow(BadRequestException.class);
        Kontaktskjema kontaktskjema = TestData.kontaktskjema();
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
        gsakOppgaveService.opprettOppgaveOgLagreStatus(TestData.kontaktskjema());
        verify(gsakKlient, times(2)).opprettGsakOppgave(any());
    }

    @Test
    public void opprettOppgaveOgLagreStatus__skal_sette_riktige_gsak_temaer_hvis_tema_er_sykefravær() {
        Kontaktskjema kontaktskjema = TestData.kontaktskjemaBuilder().temaType(TemaType.FOREBYGGE_SYKEFRAVÆR).build();

        gsakOppgaveService.opprettOppgaveOgLagreStatus(kontaktskjema);

        GsakRequest sendtRequest = capturedGsakRequest();

        assertThat(sendtRequest.getTema()).isEqualTo(GsakOppgaveService.GSAK_TEMA_INKLUDERENDE_ARBEIDSLIV);
        assertThat(sendtRequest.getTemagruppe()).isEqualTo(null);
    }

    @Test
    public void opprettOppgaveOgLagreStatus__skal_sette_riktige_gsak_temaer_hvis_tema_IKKE_er_sykefravær() {
        Kontaktskjema kontaktskjema = TestData.kontaktskjemaBuilder().temaType(TemaType.REKRUTTERING).build();

        gsakOppgaveService.opprettOppgaveOgLagreStatus(kontaktskjema);

        GsakRequest sendtRequest = capturedGsakRequest();

        assertThat(sendtRequest.getTema()).isEqualTo(GsakOppgaveService.GSAK_TEMA_OPPFØLGING_ARBEIDSGIVER);
        assertThat(sendtRequest.getTemagruppe()).isEqualTo(GsakOppgaveService.GSAK_TEMAGRUPPE_ARBEID);
    }

    @Test
    public void opprettOppgaveOgLagreStatus__skal_bruke_enhetsnr_til_arbeidslivssenteret_hvis_tema_er_sykefravær() {
        String enhetsnrTilArbeidslivssenter = "1234";
        when(navEnhetService.mapFraFylkesenhetNrTilArbeidslivssenterEnhetsnr(anyString())).thenReturn(enhetsnrTilArbeidslivssenter);

        Kontaktskjema kontaktskjema = TestData.kontaktskjemaBuilder().temaType(TemaType.FOREBYGGE_SYKEFRAVÆR).build();
        gsakOppgaveService.opprettOppgaveOgLagreStatus(kontaktskjema);

        GsakRequest sendtRequest = capturedGsakRequest();

        assertThat(sendtRequest.getTildeltEnhetsnr()).isEqualTo(enhetsnrTilArbeidslivssenter);
    }

    @Test
    public void opprettOppgaveOgLagreStatus__skal_bruke_enhetsnr_til_kommunalt_kontor_hvis_tema_IKKE_er_sykefravær() {
        String enhetsnrTilKommunaltKontor = "1234";
        when(navEnhetService.mapFraKommunenrTilEnhetsnr(anyString())).thenReturn(enhetsnrTilKommunaltKontor);

        Kontaktskjema kontaktskjema = TestData.kontaktskjemaBuilder().fylkesenhetsnr(FYLKESENHETNR_TIL_MØRE_OG_ROMSDAL).temaType(TemaType.REKRUTTERING).build();
        gsakOppgaveService.opprettOppgaveOgLagreStatus(kontaktskjema);

        GsakRequest sendtRequest = capturedGsakRequest();

        assertThat(sendtRequest.getTildeltEnhetsnr()).isEqualTo(enhetsnrTilKommunaltKontor);
    }

    private GsakRequest capturedGsakRequest() {
        verify(gsakKlient, times(1)).opprettGsakOppgave(gsakRequestArgumentCaptor.capture());
        return gsakRequestArgumentCaptor.getValue();
    }

    @Test
    public void skal_fjerne_correlationId() {
        gsakOppgaveService.opprettOppgaveOgLagreStatus(TestData.kontaktskjema());

        assertThat(MDC.get(GsakOppgaveService.CORRELATION_ID)).isNull();
    }

    @Test
    public void skal_fjerne_correlationId_ved_exception() {
        doThrow(RuntimeException.class).when(oppgaveRepository).save(any());
        try {
            gsakOppgaveService.opprettOppgaveOgLagreStatus(TestData.kontaktskjema());
        } catch (Exception e) {
        }

        assertThat(MDC.get(GsakOppgaveService.CORRELATION_ID)).isNull();
    }

    @Test
    public void opprettOppgaveOgLagreStatus__skal_ikke_opprette_oppgave_for_andre_fylker_enn_Møre_og_Romsdal() {
        Kontaktskjema kontaktskjema = TestData.kontaktskjemaBuilder()
                .id(5)
                .fylkesenhetsnr("1600")
                .temaType(TemaType.REKRUTTERING)
                .build();
        gsakOppgaveService.opprettOppgaveOgLagreStatus(kontaktskjema);

        verify(gsakKlient, times(0)).opprettGsakOppgave(any());
        verify(oppgaveRepository).save(eq(GsakOppgave.builder().gsakId(null).kontaktskjemaId(5).status(GsakOppgave.OppgaveStatus.SKAL_IKKE_SENDES).opprettet(dateProvider.now()).build()));

    }
}
