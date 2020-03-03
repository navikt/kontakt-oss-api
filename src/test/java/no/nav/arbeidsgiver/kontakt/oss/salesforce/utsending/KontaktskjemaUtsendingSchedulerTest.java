package no.nav.arbeidsgiver.kontakt.oss.salesforce.utsending;

import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import no.nav.arbeidsgiver.kontakt.oss.KontaktskjemaRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class KontaktskjemaUtsendingSchedulerTest {

    @Mock
    KontaktskjemaRepository kontaktskjemaRepository;
    @Mock
    KontaktskjemaUtsendingService kontaktskjemaUtsendingService;
    @Mock
    LockingTaskExecutor taskExecutor;


    @Test
    public void skalSjekkeAtScheduledMetodeBrukerShedlock() {
        KontaktskjemaUtsendingScheduler kontaktskjemaUtsendingScheduler = new KontaktskjemaUtsendingScheduler(kontaktskjemaUtsendingService, kontaktskjemaRepository, taskExecutor);

        kontaktskjemaUtsendingScheduler.scheduledSendSkjemaTilSalesForce();

        verify(taskExecutor).executeWithLock(any(Runnable.class), any(LockConfiguration.class));
    }
}
