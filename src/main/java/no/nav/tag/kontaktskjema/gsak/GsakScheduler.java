package no.nav.tag.kontaktskjema.gsak;


import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import no.nav.tag.kontaktskjema.Kontaktskjema;
import no.nav.tag.kontaktskjema.KontaktskjemaRepository;

@Slf4j
@Component
public class GsakScheduler {

    private static final String ONE_MIN = "PT14M";

    private static final String THIRTY_SECONDS = "PT30S";
    
    @Autowired
    private KontaktskjemaRepository kontaktskjemaRepository;    
    
    @Autowired
    private GsakOppgaveForSkjema oppgaveForSkjema;    
    
    @Scheduled(cron = "* * * * * ?")
    @SchedulerLock(name = "opprettOppgaveForSkjemaer", lockAtMostForString = ONE_MIN, lockAtLeastForString = THIRTY_SECONDS)
    public void scheduledOpprettOppgaveForSkjemaer() {
        Collection<Kontaktskjema> skjemaer = kontaktskjemaRepository.findAllWithNoGsakOppgave();
        log.info("Fant {} skjemaer som ikke har gsak-oppgave", skjemaer.size());
        skjemaer.forEach(oppgaveForSkjema::opprettOppgaveOgLagreStatus);
    }
}
