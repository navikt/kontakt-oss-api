package no.nav.tag.kontakt.oss.gsak;

import no.nav.tag.kontakt.oss.DateProvider;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.enhetsmapping.EnhetUtils;
import no.nav.tag.kontakt.oss.gsak.integrasjon.GsakKlient;
import org.junit.Test;

import java.time.LocalDateTime;

import static no.nav.tag.kontakt.oss.gsak.GsakOppgave.OppgaveStatus.OK;
import static org.mockito.Mockito.*;

public class GsakOppgaveServiceTest {
    @Test
    public void skalOppdatereDatabaseEtterKallTilGsak() {
        LocalDateTime now = LocalDateTime.now();
        DateProvider dateProvider = mock(DateProvider.class);
        when(dateProvider.now()).thenReturn(now);
        GsakOppgaveRepository oppgaveRepository = mock(GsakOppgaveRepository.class);

        GsakOppgaveService gsakOppgaveForSkjema = new GsakOppgaveService(
                oppgaveRepository,
                dateProvider,
                mock(GsakKlient.class),
                mock(EnhetUtils.class)
        );

        Kontaktskjema lagKontaktskjema = Kontaktskjema.builder().id(5).build();
        gsakOppgaveForSkjema.opprettOppgaveOgLagreStatus(lagKontaktskjema);

        verify(oppgaveRepository).save(eq(GsakOppgave.builder().gsakId(0).kontaktskjemaId(5).status(OK).opprettet(dateProvider.now()).build()));

    }
}