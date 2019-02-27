package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FylkesinndelingController {

    private final NorgService norgService;
    private final FylkesinndelingRepository fylkesinndelingRepository;

    public FylkesinndelingController(NorgService norgService, FylkesinndelingRepository fylkesinndelingRepository) {
        this.norgService = norgService;
        this.fylkesinndelingRepository = fylkesinndelingRepository;
    }

    @GetMapping(value = "${controller.basepath}/fylkerOgKommuner")
    public FylkesinndelingMedNavEnheter hentFylkerOgKommuner() {
        // TODO TAG-311 Før dette endepunktet kan tas i bruk i frontend, må NORG-kallene caches.
        List<KommuneEllerBydel> kommunerOgBydeler = norgService.hentListeOverAlleKommunerOgBydeler();
        return new FylkesinndelingMedNavEnheter(
                norgService.hentMapFraNavenhetTilFylkesenhet(),
                norgService.hentMapFraKommuneEllerBydelTilNavenhet(kommunerOgBydeler),
                kommunerOgBydeler
        );
    }
}
