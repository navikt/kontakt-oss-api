package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

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

    public static String FYLKESINNDELING_SHEDLOCK_NAVN = "oppdaterFylkesinndeling";

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

        int hourInSeconds = 60 * 60;

        Instant lockAtMostUntil = Instant.now().plusSeconds(28 * hourInSeconds);
        Instant lockAtLeastUntil = Instant.now().plusSeconds(24 * hourInSeconds);

        taskExecutor.executeWithLock(
                (Runnable) fylkesinndelingService::oppdaterFylkesinndeling,
                new LockConfiguration(FYLKESINNDELING_SHEDLOCK_NAVN, lockAtMostUntil, lockAtLeastUntil)
        );
    }

    private void oppdaterFylkesinndelingUtenomSchedule() {
        Instant lockAtMostUntil = Instant.now().plusSeconds(10 * 60);
        Instant lockAtLeastUntil = Instant.now().plusSeconds(5 * 60);

        taskExecutor.executeWithLock(
                (Runnable)() -> {
                    log.info("Tvinger oppdatering av fylkesinndeling");
                    fylkesinndelingService.oppdaterFylkesinndeling();
                },
                new LockConfiguration("opprettOppgaveForSkjemaer-OVERRIDE", lockAtMostUntil, lockAtLeastUntil)
        );
    }
}
