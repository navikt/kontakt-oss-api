package no.nav.arbeidsgiver.kontakt.oss.gsak;

import no.bekk.bekkopen.org.OrganisasjonsnummerCalculator;
import no.nav.arbeidsgiver.kontakt.oss.DateProvider;
import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;
import no.nav.arbeidsgiver.kontakt.oss.TemaType;
import no.nav.arbeidsgiver.kontakt.oss.gsak.integrasjon.GsakKlient;
import no.nav.arbeidsgiver.kontakt.oss.gsak.integrasjon.GsakRequest;
import no.nav.arbeidsgiver.kontakt.oss.navenhetsmapping.NavEnhetService;
import no.nav.arbeidsgiver.kontakt.oss.testUtils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
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
