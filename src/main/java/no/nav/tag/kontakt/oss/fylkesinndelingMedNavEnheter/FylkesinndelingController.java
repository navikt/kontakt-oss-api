package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FylkesinndelingController {

    private final FylkesinndelingRepository fylkesinndelingRepository;

    public FylkesinndelingController(FylkesinndelingRepository fylkesinndelingRepository) {
        this.fylkesinndelingRepository = fylkesinndelingRepository;
    }

    @GetMapping(value = "${controller.basepath}/fylkerOgKommuner")
    public FylkesinndelingMedNavEnheter hentFylkerOgKommuner() {
        return fylkesinndelingRepository.hentFylkesinndeling();
    }
}
