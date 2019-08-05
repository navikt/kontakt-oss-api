package no.nav.tag.kontakt.oss.metrics;

import org.junit.Test;

import no.nav.tag.kontakt.oss.events.GsakOppgaveSendt;
import no.nav.tag.kontakt.oss.gsak.GsakOppgave.OppgaveStatus;
import no.nav.tag.kontakt.oss.gsak.GsakOppgaveService;
import no.nav.tag.kontakt.oss.testUtils.TestData;

public class MetricsListenersTest {

    @Test    
    public void testGsakOppgaveSendt() {
        //Svært enkel test for å kunne observere hvordan logglinjen vil se ut, og for å verifisere at enkelte data kan være null
        new MetricsListeners().gsakOppgaveSendt(new GsakOppgaveSendt(new GsakOppgaveService.Behandlingsresultat(OppgaveStatus.OK, 1), null));
        new MetricsListeners().gsakOppgaveSendt(new GsakOppgaveSendt(new GsakOppgaveService.Behandlingsresultat(OppgaveStatus.FEILET, null), null));
        new MetricsListeners().gsakOppgaveSendt(new GsakOppgaveSendt(new GsakOppgaveService.Behandlingsresultat(OppgaveStatus.DISABLED, null), null));
        new MetricsListeners().gsakOppgaveSendt(new GsakOppgaveSendt(new GsakOppgaveService.Behandlingsresultat(OppgaveStatus.OK, 1), TestData.gsakRequest()));
    }
}
