package no.nav.tag.kontakt.oss.gsak;

import static no.nav.tag.kontakt.oss.gsak.GsakOppgave.OppgaveStatus.DISABLED;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import no.nav.tag.kontakt.oss.DateProvider;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import org.junit.Test;

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
