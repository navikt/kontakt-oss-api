package no.nav.tag.kontaktskjema.gsak;

import static no.nav.tag.kontaktskjema.gsak.GsakOppgave.OppgaveStatus.DISABLED;
import static org.mockito.Mockito.*;

import org.junit.Test;

import no.nav.tag.kontaktskjema.Kontaktskjema;

public class GsakOppgaveForSkjemaTest {

    @Test
    public void skalOppdatereDatabaseEtterKallTilGsak() {
        GsakOppgaveForSkjema gsakOppgaveForSkjema = new GsakOppgaveForSkjema();
        gsakOppgaveForSkjema.oppgaveRepository = mock(GsakOppgaveRepository.class);

        Kontaktskjema lagKontaktskjema = Kontaktskjema.builder().id(5).build();
        gsakOppgaveForSkjema.opprettOppgaveOgLagreStatus(lagKontaktskjema);

        verify(gsakOppgaveForSkjema.oppgaveRepository).save(eq(GsakOppgave.builder().kontaktskjemaId(5).status(DISABLED).build()));

    }
}
