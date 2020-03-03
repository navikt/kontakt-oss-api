package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
public class FylkesinndelingScheduler {

    private final LockingTaskExecutor taskExecutor;
    private final FylkesinndelingService fylkesinndelingService;
    private static final int HOUR = 60 * 60;
    private static final int MINUTE = 60;

    private static final String FYLKESINNDELING_SHEDLOCK_NAVN = "oppdaterFylkesinndeling";

    @Autowired
    public FylkesinndelingScheduler(
            LockingTaskExecutor taskExecutor,
            FylkesinndelingService fylkesinndelingService,
            @Value("${FYLKESINNDELING_TVING_OPPDATERING:false}") String tvingOppdatering) {
        this.taskExecutor = taskExecutor;
        this.fylkesinndelingService = fylkesinndelingService;

        if ("true".equals(tvingOppdatering)) {
            oppdaterFylkesinndelingUtenomSchedule();
        }
    }

    @Scheduled(fixedRateString = "${norg.fixed-rate}")
    public void scheduledOppdaterInformasjonFraNorg() {
        log.info("Sjekker shedlock for fylkesinndeling-oppdatering");

        Instant lockAtMostUntil = Instant.now().plusSeconds(28 * HOUR);
        Instant lockAtLeastUntil = Instant.now().plusSeconds(24 * HOUR);

        taskExecutor.executeWithLock(
                (Runnable) fylkesinndelingService::oppdaterFylkesinndeling,
                new LockConfiguration(FYLKESINNDELING_SHEDLOCK_NAVN, lockAtMostUntil, lockAtLeastUntil)
        );
    }

    private void oppdaterFylkesinndelingUtenomSchedule() {
        Instant lockAtMostUntil = Instant.now().plusSeconds(10 * MINUTE);
        Instant lockAtLeastUntil = Instant.now().plusSeconds(5 * MINUTE);

        taskExecutor.executeWithLock(
                (Runnable) () -> {
                    log.info("Tvinger oppdatering av fylkesinndeling");
                    fylkesinndelingService.oppdaterFylkesinndeling();
                },
                new LockConfiguration(FYLKESINNDELING_SHEDLOCK_NAVN + "-OVERRIDE", lockAtMostUntil, lockAtLeastUntil)
        );
    }
}
