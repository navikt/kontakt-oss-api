package no.nav.arbeidsgiver.kontakt.oss.salesforce.utsending;


import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;
import no.nav.arbeidsgiver.kontakt.oss.KontaktskjemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;

@Slf4j
@Component
public class KontaktskjemaUtsendingScheduler {

    private final KontaktskjemaUtsendingService kontaktskjemaUtsendingService;
    private final LockingTaskExecutor taskExecutor;
    private final KontaktskjemaRepository kontaktskjemaRepository;

    @Autowired
    public KontaktskjemaUtsendingScheduler(KontaktskjemaUtsendingService kontaktskjemaUtsendingService, KontaktskjemaRepository kontaktskjemaRepository, LockingTaskExecutor taskExecutor) {
        this.kontaktskjemaUtsendingService = kontaktskjemaUtsendingService;
        this.kontaktskjemaRepository = kontaktskjemaRepository;
        this.taskExecutor = taskExecutor;
    }

    @Scheduled(cron = "* * * * * ?")
    public void scheduledSendSkjemaTilSalesForce() {

        Instant lockAtMostUntil = Instant.now().plusSeconds(60);
        Instant lockAtLeastUntil = Instant.now().plusSeconds(30);

        taskExecutor.executeWithLock(
                (Runnable) this::sendSkjemaTilSalesForce,
                new LockConfiguration("utsendingAvSkjemaerTilSalesforce", lockAtMostUntil, lockAtLeastUntil)
        );

    }

    public void sendSkjemaTilSalesForce() {
        Collection<Kontaktskjema> skjemaer = kontaktskjemaRepository.hentKontakskjemaerSomSkalSendesTilSalesforce();

        if (skjemaer.size() > 0) {
            log.info("Fant {} skjemaer som skal sendes til Salesforce", skjemaer.size());
        }

        skjemaer.forEach(
                skjema -> kontaktskjemaUtsendingService.sendSkjemaTilSalesForce(skjema));
    }

}
