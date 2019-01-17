package no.nav.tag.kontaktskjema.gsak;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;

@Slf4j
@Component
public class GsakScheduler {

    private static final String ONE_MIN = "PT14M";

    private static final String THIRTY_SECONDS = "PT30S";

    @Scheduled(cron = "* * * * * ?")
    @SchedulerLock(name = "navn", lockAtMostForString = ONE_MIN, lockAtLeastForString = THIRTY_SECONDS)
    public void scheduledBehandleSkjemaer() {
        log.debug("Kjører jobb for gsak");
    }
}
