package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter;

import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FylkesinndelingSchedulerTest {

    @Mock
    private LockingTaskExecutor taskExecutor;

    @Mock
    private FylkesinndelingService fylkesinndelingService;

    @Captor
    private ArgumentCaptor<Runnable> captor;

    @Test
    public void scheduledOppdaterInformasjonFraNorg__skal_oppdatere_fylkesinndeling_med_lock() {
        FylkesinndelingScheduler scheduler = new FylkesinndelingScheduler(
                taskExecutor,
                fylkesinndelingService,
                "false");

        scheduler.scheduledOppdaterInformasjonFraNorg();

        verify(taskExecutor).executeWithLock(captor.capture(), any(LockConfiguration.class));

        Runnable oppdaterFylkesinndelingMedLock = captor.getValue();
        oppdaterFylkesinndelingMedLock.run();

        verify(fylkesinndelingService).oppdaterFylkesinndeling();
    }
}
