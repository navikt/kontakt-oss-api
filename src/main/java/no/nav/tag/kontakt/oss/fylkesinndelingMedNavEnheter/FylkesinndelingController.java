package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgKlient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class FylkesinndelingController {

    private final FylkesinndelingRepository fylkesinndelingRepository;
    private final NorgKlient norgKlient;

    public FylkesinndelingController(FylkesinndelingRepository fylkesinndelingRepository, NorgKlient norgKlient) {
        this.fylkesinndelingRepository = fylkesinndelingRepository;
        this.norgKlient = norgKlient;
    }

    @GetMapping(value = "${controller.basepath}/fylkerOgKommuner")
    public Map<String, List<KommuneEllerBydel>> hentFylkerOgKommuner() {
        return fylkesinndelingRepository.hentFylkesinndeling().getFylkeTilKommuneEllerBydel();
    }

    @GetMapping(value = "${controller.basepath}/geografi")
    public Map<String, NavEnhet> testHentGeografi() {
        return fylkesinndelingRepository.hentKommuneNrEllerBydelNrTilNavEnhet();
    }
}
