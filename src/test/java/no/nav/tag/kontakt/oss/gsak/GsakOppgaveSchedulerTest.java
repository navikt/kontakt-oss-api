package no.nav.tag.kontakt.oss.gsak;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import no.nav.tag.kontakt.oss.KontaktskjemaRepository;
import org.junit.Test;

import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GsakOppgaveSchedulerTest {

    @Mock KontaktskjemaRepository kontaktskjemaRepository;
    @Mock GsakOppgaveService oppgaveForSkjema;
    @Mock LockingTaskExecutor taskExecutor;

    @Test
    public void skalSjekkeAtSceduledMetodeBrukerShedlock() {
        GsakOppgaveScheduler gsakScheduler = new GsakOppgaveScheduler(kontaktskjemaRepository, oppgaveForSkjema, taskExecutor);

        gsakScheduler.scheduledOpprettOppgaveForSkjemaer();
        
        verify(taskExecutor).executeWithLock(any(Runnable.class), any(LockConfiguration.class));
    }

}
