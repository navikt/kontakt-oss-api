package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgKlient;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgOrganisering;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GeografiController {

    private final NorgService norgService;
    private final NorgKlient norgKlient;

    public GeografiController(NorgService norgService, NorgKlient norgKlient) {
        this.norgService = norgService;
        this.norgKlient = norgKlient;
    }

    @GetMapping(value = "${controller.basepath}/geografi")
    public FylkesinndelingMedNavEnheter geografi() {
        List<KommuneEllerBydel> kommunerOgBydeler = norgService.hentListeOverAlleKommunerOgBydeler();
        return new FylkesinndelingMedNavEnheter(
                norgService.hentMapFraNavenhetTilFylkesenhet(),
                norgService.hentMapFraKommuneEllerBydelTilNavenhet(kommunerOgBydeler),
                kommunerOgBydeler
        );
    }

    @GetMapping(value = "${controller.basepath}/geografi")
    public List<NorgOrganisering> organisering() {
        return norgKlient.hentOrganiseringFraNorg();
    }
}
