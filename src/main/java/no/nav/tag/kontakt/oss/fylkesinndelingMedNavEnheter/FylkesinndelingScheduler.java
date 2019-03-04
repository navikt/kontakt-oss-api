package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FylkesinndelingScheduler {
    private final FylkesinndelingRepository fylkesinndelingRepository;
    private final LockingTaskExecutor taskExecutor;
    private final NorgService norgService;

    public FylkesinndelingScheduler(FylkesinndelingRepository fylkesinndelingRepository, LockingTaskExecutor taskExecutor, NorgService norgService) {
        this.fylkesinndelingRepository = fylkesinndelingRepository;
        this.taskExecutor = taskExecutor;
        this.norgService = norgService;
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void scheduledOppdaterInformasjonFraNorg() {

        int hourInSeconds = 60 * 60;

        Instant lockAtMostUntil = Instant.now().plusSeconds(28 * hourInSeconds);
        Instant lockAtLeastUntil = Instant.now().plusSeconds(24 * hourInSeconds);

        taskExecutor.executeWithLock(
                (Runnable)this::oppdaterInformasjonFraNorg,
                new LockConfiguration("oppdaterInformasjonFraNorg", lockAtMostUntil, lockAtLeastUntil)
        );

    }

    private void oppdaterInformasjonFraNorg() {
        List<KommuneEllerBydel> kommunerOgBydeler = norgService.hentListeOverAlleKommunerOgBydeler();
        Map<KommuneEllerBydel, NavEnhet> fraKommuneEllerBydelTilNavEnhet = norgService.hentMapFraKommuneEllerBydelTilNavEnhet(kommunerOgBydeler);
        FylkesinndelingMedNavEnheter fylkesinndeling = new FylkesinndelingMedNavEnheter(
                norgService.hentMapFraNavenhetTilFylkesenhet(),
                fraKommuneEllerBydelTilNavEnhet,
                kommunerOgBydeler
        );

        fylkesinndelingRepository.oppdaterInformasjonFraNorg(
                fylkesinndeling,
                fraKommuneEllerBydelTilNavEnhet.keySet().stream().collect(Collectors.toMap(
                        KommuneEllerBydel::getNummer,
                        kommunerEllerBydel -> fraKommuneEllerBydelTilNavEnhet.get(kommunerEllerBydel)
                ))
        );
    }
}
