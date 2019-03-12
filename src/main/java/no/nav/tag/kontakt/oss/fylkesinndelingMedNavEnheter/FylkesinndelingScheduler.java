package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FylkesinndelingScheduler {
    private final FylkesinndelingRepository fylkesinndelingRepository;
    private final LockingTaskExecutor taskExecutor;
    private final FylkesinndelingService norgService;

    public FylkesinndelingScheduler(FylkesinndelingRepository fylkesinndelingRepository, LockingTaskExecutor taskExecutor, FylkesinndelingService norgService) {
        this.fylkesinndelingRepository = fylkesinndelingRepository;
        this.taskExecutor = taskExecutor;
        this.norgService = norgService;
    }

    @Scheduled(fixedRateString = "${norg.fixed-rate}")
    public void scheduledOppdaterInformasjonFraNorg() {
        log.info("Sjekker shedlock for NORG-oppdatering");

        int hourInSeconds = 60 * 60;

        Instant lockAtMostUntil = Instant.now().plusSeconds(28 * hourInSeconds);
        Instant lockAtLeastUntil = Instant.now().plusSeconds(24 * hourInSeconds);

        taskExecutor.executeWithLock(
                (Runnable)this::oppdaterInformasjonFraNorg,
                new LockConfiguration("oppdaterInformasjonFraNorg", lockAtMostUntil, lockAtLeastUntil)
        );
    }

    private void oppdaterInformasjonFraNorg() {
        log.info("Oppdaterer informasjon fra NORG");

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
