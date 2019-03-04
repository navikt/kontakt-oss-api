package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class FylkesinndelingController {

    private final FylkesinndelingRepository fylkesinndelingRepository;

    public FylkesinndelingController(FylkesinndelingRepository fylkesinndelingRepository) {
        this.fylkesinndelingRepository = fylkesinndelingRepository;
    }

    @GetMapping(value = "${controller.basepath}/fylkerOgKommuner")
    public Map<String, List<KommuneEllerBydel>> hentFylkerOgKommuner() {
        return fylkesinndelingRepository.hentFylkesinndeling().getFylkeTilKommuneEllerBydel();
    }
}
