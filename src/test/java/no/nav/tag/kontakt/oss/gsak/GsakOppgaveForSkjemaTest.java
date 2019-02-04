package no.nav.tag.kontakt.oss.gsak;

import no.nav.tag.kontakt.oss.DateProvider;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import org.junit.Test;

import java.time.LocalDateTime;

import static no.nav.tag.kontakt.oss.gsak.GsakOppgave.OppgaveStatus.DISABLED;
import static org.mockito.Mockito.*;

public class GsakOppgaveForSkjemaTest {
    @Test
    public void skalOppdatereDatabaseEtterKallTilGsak() {
        LocalDateTime now = LocalDateTime.now();
        DateProvider dateProvider = mock(DateProvider.class);
        when(dateProvider.now()).thenReturn(now);
        GsakOppgaveRepository oppgaveRepository = mock(GsakOppgaveRepository.class);

        GsakOppgaveForSkjema gsakOppgaveForSkjema = new GsakOppgaveForSkjema(
                oppgaveRepository,
                dateProvider,
                mock(GsakKlient.class)
        );

        Kontaktskjema lagKontaktskjema = Kontaktskjema.builder().id(5).build();
        gsakOppgaveForSkjema.opprettOppgaveOgLagreStatus(lagKontaktskjema);

        verify(oppgaveRepository).save(eq(GsakOppgave.builder().kontaktskjemaId(5).status(DISABLED).opprettet(dateProvider.now()).build()));

    }
}
