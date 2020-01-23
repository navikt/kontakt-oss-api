package no.nav.tag.kontakt.oss.salesforce.utsending;

import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import no.nav.tag.kontakt.oss.KontaktskjemaRepository;
import no.nav.tag.kontakt.oss.salesforce.SalesforceService;
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
    KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository;
    @Mock
    SalesforceService salesforceService;
    @Mock
    LockingTaskExecutor taskExecutor;

    @Test
    public void skalSjekkeAtSceduledMetodeBrukerShedlock() {
        KontaktskjemaUtsendingScheduler kontaktskjemaUtsendingScheduler = new KontaktskjemaUtsendingScheduler(kontaktskjemaRepository, kontaktskjemaUtsendingRepository, salesforceService, taskExecutor);

        kontaktskjemaUtsendingScheduler.scheduledSendSkjemaTilSalesForce();

        verify(taskExecutor).executeWithLock(any(Runnable.class), any(LockConfiguration.class));
    }


}