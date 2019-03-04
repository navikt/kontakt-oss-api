package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FylkesinndelingSchedulerTest {

    @Mock private FylkesinndelingRepository fylkesinndelingRepository;
    @Mock private LockingTaskExecutor taskExecutor;
    @Mock private NorgService norgService;

    @Test
    public void skalSjekkeAtSceduledMetodeBrukerShedlock() {
        FylkesinndelingScheduler scheduler = new FylkesinndelingScheduler(
                fylkesinndelingRepository,
                taskExecutor,
                norgService
        );

        scheduler.scheduledOppdaterInformasjonFraNorg();

        verify(taskExecutor).executeWithLock(any(Runnable.class), any(LockConfiguration.class));
    }
}