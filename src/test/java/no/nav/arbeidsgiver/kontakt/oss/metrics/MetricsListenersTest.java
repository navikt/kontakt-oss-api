package no.nav.arbeidsgiver.kontakt.oss.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.arbeidsgiver.kontakt.oss.events.GsakOppgaveSendt;
import no.nav.arbeidsgiver.kontakt.oss.gsak.GsakOppgave;
import no.nav.arbeidsgiver.kontakt.oss.gsak.GsakOppgaveService;
import no.nav.arbeidsgiver.kontakt.oss.testUtils.TestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class MetricsListenersTest {

    @Mock
    private MeterRegistry meterRegistry;

    @Test
    public void testGsakOppgaveSendt() {
        //Svært enkel test for å kunne observere hvordan logglinjen vil se ut, og for å verifisere at enkelte data kan være null
        new MetricsListeners(meterRegistry).gsakOppgaveSendt(new GsakOppgaveSendt(new GsakOppgaveService.Behandlingsresultat(GsakOppgave.OppgaveStatus.OK, 1), null));
        new MetricsListeners(meterRegistry).gsakOppgaveSendt(new GsakOppgaveSendt(new GsakOppgaveService.Behandlingsresultat(GsakOppgave.OppgaveStatus.FEILET, null), null));
        new MetricsListeners(meterRegistry).gsakOppgaveSendt(new GsakOppgaveSendt(new GsakOppgaveService.Behandlingsresultat(GsakOppgave.OppgaveStatus.OK, 1), TestData.gsakRequest()));
    }
}
