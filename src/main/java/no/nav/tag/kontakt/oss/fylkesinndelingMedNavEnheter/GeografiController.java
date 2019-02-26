package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GeografiController {

    private final NorgService norgService;

    public GeografiController(NorgService norgService) {
        this.norgService = norgService;
    }

    @GetMapping(value = "${controller.basepath}/fylkerOgKommuner")
    public FylkesinndelingMedNavEnheter geografi() {
        // TODO TAG-309 Før dette endepunktet kan tas i bruk i frontend, må NORG-kallene caches.
        List<KommuneEllerBydel> kommunerOgBydeler = norgService.hentListeOverAlleKommunerOgBydeler();
        return new FylkesinndelingMedNavEnheter(
                norgService.hentMapFraNavenhetTilFylkesenhet(),
                norgService.hentMapFraKommuneEllerBydelTilNavenhet(kommunerOgBydeler),
                kommunerOgBydeler
        );
    }
}
