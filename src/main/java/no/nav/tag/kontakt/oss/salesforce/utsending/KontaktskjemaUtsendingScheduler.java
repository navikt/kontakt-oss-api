package no.nav.tag.kontakt.oss.salesforce.utsending;


import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.KontaktskjemaRepository;
import no.nav.tag.kontakt.oss.salesforce.SalesforceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;

@Slf4j
@Component
public class KontaktskjemaUtsendingScheduler {

    private final KontaktskjemaRepository kontaktskjemaRepository;
    private final KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository;
    private final SalesforceService salesforceService;
    private final LockingTaskExecutor taskExecutor;

    @Autowired
    public KontaktskjemaUtsendingScheduler(
            KontaktskjemaRepository kontaktskjemaRepository,
            KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository,
            SalesforceService salesforceService,
            LockingTaskExecutor taskExecutor) {
        this.kontaktskjemaRepository = kontaktskjemaRepository;
        this.kontaktskjemaUtsendingRepository = kontaktskjemaUtsendingRepository;
        this.salesforceService = salesforceService;
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

    private void sendSkjemaTilSalesForce() {
        Collection<Kontaktskjema> skjemaer = kontaktskjemaRepository.hentKontakskjemaerSomSkalSendesTilSalesforce();

        if (skjemaer.size() > 0) {
            log.info("Fant {} skjemaer som skal sendes til Salesforce", skjemaer.size());
        }

        skjemaer.forEach(
                skjema -> {
                    salesforceService.sendKontaktskjemaTilSalesforce(skjema);
                    KontaktskjemaUtsending kontaktskjemaUtsending =
                            kontaktskjemaUtsendingRepository.hentKontakskjemaUtsending(
                                    skjema.getId()
                            );
                    kontaktskjemaUtsendingRepository.save(KontaktskjemaUtsending.sent(kontaktskjemaUtsending));
                });
    }
}
