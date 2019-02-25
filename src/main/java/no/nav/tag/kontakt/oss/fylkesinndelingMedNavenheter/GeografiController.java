package no.nav.tag.kontakt.oss.fylkesinndelingMedNavenheter;

import no.nav.tag.kontakt.oss.fylkesinndelingMedNavenheter.integrasjon.NorgService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeografiController {

    private final NorgService norgService;

    public GeografiController(NorgService norgService) {
        this.norgService = norgService;
    }

    @GetMapping(value = "${controller.basepath}/geografi")
    public Object geografi() {
        return norgService.hentListeOverAlleKommunerOgBydeler();
    }
}
