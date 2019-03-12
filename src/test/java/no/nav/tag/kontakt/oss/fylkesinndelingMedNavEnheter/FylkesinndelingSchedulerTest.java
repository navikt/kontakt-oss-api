package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FylkesinndelingSchedulerTest {

    @Mock private FylkesinndelingRepository fylkesinndelingRepository;
    @Mock private LockingTaskExecutor taskExecutor;
    @Mock private FylkesinndelingService norgService;

    @Test
    public void skalSjekkeAtSceduledMetodeBrukerShedlock() {
        FylkesinndelingScheduler scheduler = new FylkesinndelingScheduler(
                fylkesinndelingRepository,
                taskExecutor,
                norgService,
                "false"
        );

        scheduler.scheduledOppdaterInformasjonFraNorg();

        verify(taskExecutor).executeWithLock(any(Runnable.class), any(LockConfiguration.class));
    }

    @Test
    public void konstruktor__skal_fjerne_shedlock_hvis_tvingOppdatering_er_true() {
        new FylkesinndelingScheduler(
                fylkesinndelingRepository,
                taskExecutor,
                norgService,
                "true"
        );
        verify(fylkesinndelingRepository, times(1)).fjernShedlock();
    }

    @Test
    public void konstruktor__skal_IKKE_fjerne_shedlock_hvis_tvingOppdatering_er_false() {
        new FylkesinndelingScheduler(
                fylkesinndelingRepository,
                taskExecutor,
                norgService,
                "false"
        );
        verify(fylkesinndelingRepository, times(0)).fjernShedlock();
    }
}