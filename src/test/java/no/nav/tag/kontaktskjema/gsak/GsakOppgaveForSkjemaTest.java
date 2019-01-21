package no.nav.tag.kontaktskjema.gsak;

import static no.nav.tag.kontaktskjema.gsak.GsakOppgave.OppgaveStatus.DISABLED;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.Test;

import no.nav.tag.kontaktskjema.DateProvider;
import no.nav.tag.kontaktskjema.Kontaktskjema;

public class GsakOppgaveForSkjemaTest {

    @Test
    public void skalOppdatereDatabaseEtterKallTilGsak() {
        GsakOppgaveForSkjema gsakOppgaveForSkjema = new GsakOppgaveForSkjema();
        gsakOppgaveForSkjema.oppgaveRepository = mock(GsakOppgaveRepository.class);
        
        gsakOppgaveForSkjema.dateProvider = mock(DateProvider.class);
        LocalDateTime now = LocalDateTime.now();
        when(gsakOppgaveForSkjema.dateProvider.now()).thenReturn(now);
        
        
        Kontaktskjema lagKontaktskjema = Kontaktskjema.builder().id(5).build();
        gsakOppgaveForSkjema.opprettOppgaveOgLagreStatus(lagKontaktskjema);

        verify(gsakOppgaveForSkjema.oppgaveRepository).save(eq(GsakOppgave.builder().kontaktskjemaId(5).status(DISABLED).opprettet(now).build()));

    }
}
