package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class FylkesinndelingController {

    private final NorgService norgService;

    public FylkesinndelingController(NorgService norgService) {
        this.norgService = norgService;
    }

    @GetMapping(value = "${controller.basepath}/kommunerOgBydeler")
    public Map<String, List<KommuneEllerBydel>> hentFylkerOgKommuner() {
        // TODO TAG-311 Før dette endepunktet kan tas i bruk i frontend, må NORG-kallene caches.
        List<KommuneEllerBydel> kommunerOgBydeler = norgService.hentListeOverAlleKommunerOgBydeler();
        return new FylkesinndelingMedNavEnheter(
                norgService.hentMapFraNavenhetTilFylkesenhet(),
                norgService.hentMapFraKommuneEllerBydelTilNavenhet(kommunerOgBydeler),
                kommunerOgBydeler
        ).getFylkeTilKommuneEllerBydel();
    }
}
