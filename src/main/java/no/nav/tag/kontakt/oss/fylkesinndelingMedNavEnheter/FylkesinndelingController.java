package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgGeografi;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgKlient;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgOrganisering;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class FylkesinndelingController {

    private final FylkesinndelingRepository fylkesinndelingRepository;
    private final NorgKlient norgKlient;
    private final NorgService norgService;

    public FylkesinndelingController(FylkesinndelingRepository fylkesinndelingRepository, NorgKlient norgKlient, NorgService norgService) {
        this.fylkesinndelingRepository = fylkesinndelingRepository;
        this.norgKlient = norgKlient;
        this.norgService = norgService;
    }

    @GetMapping(value = "${controller.basepath}/fylkerOgKommuner")
    public Map<String, List<KommuneEllerBydel>> hentFylkerOgKommuner() {
        return fylkesinndelingRepository.hentFylkesinndeling().getFylkeTilKommuneEllerBydel();
    }

    /* TODO TAG-332 Slett disse test-endepunktene */
    @GetMapping(value = "${controller.basepath}/geografi")
    public List<NorgGeografi> testHentGeografi() {
        return norgKlient.hentGeografiFraNorg();
    }

    @GetMapping(value = "${controller.basepath}/organisering")
    public List<NorgOrganisering> testHentOrg() {
        return norgKlient.hentOrganiseringFraNorg();
    }

    @GetMapping(value = "${controller.basepath}/map1")
    public List<KommuneEllerBydel> testMap1() {
        return norgService.hentListeOverAlleKommunerOgBydeler();
    }

    @GetMapping(value = "${controller.basepath}/map")
    public Map<KommuneEllerBydel, NavEnhet> testMap() {
        return norgKlient.hentMapFraKommuneEllerBydelTilNavenhet(norgService.hentListeOverAlleKommunerOgBydeler());
    }
}
