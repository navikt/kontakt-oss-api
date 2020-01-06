package no.nav.tag.kontakt.oss.gsak;


import java.time.Instant;
import java.util.Collection;

import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.KontaktskjemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;

@Slf4j
@Component
public class GsakOppgaveScheduler {
    
    private final KontaktskjemaRepository kontaktskjemaRepository;
    private final GsakOppgaveService oppgaveForSkjema;
    private final LockingTaskExecutor taskExecutor;

    @Autowired
    public GsakOppgaveScheduler(KontaktskjemaRepository kontaktskjemaRepository, GsakOppgaveService oppgaveForSkjema, LockingTaskExecutor taskExecutor) {
        this.kontaktskjemaRepository = kontaktskjemaRepository;
        this.oppgaveForSkjema = oppgaveForSkjema;
        this.taskExecutor = taskExecutor;
    }

    @Scheduled(cron = "* * * * * ?")
    public void scheduledOpprettOppgaveForSkjemaer() {

        // TODO Sett til 60 og 30 sekunder etter nyttår 2019.
        Instant lockAtMostUntil = Instant.now().plusSeconds(1800);
        Instant lockAtLeastUntil = Instant.now().plusSeconds(1500);

        taskExecutor.executeWithLock(
                (Runnable)this::opprettOppgaveForSkjemaer,
                new LockConfiguration("opprettOppgaveForSkjemaer", lockAtMostUntil, lockAtLeastUntil)
        );

    }
    
    private void opprettOppgaveForSkjemaer() {
        Collection<Kontaktskjema> skjemaer = kontaktskjemaRepository.findAllWithNoGsakOppgave();
        if(skjemaer.size() > 0) {
            log.info("Fant {} skjemaer som ikke har gsak-oppgave", skjemaer.size());
        }
        skjemaer.forEach(oppgaveForSkjema::opprettOppgaveOgLagreStatus);
    }
}
