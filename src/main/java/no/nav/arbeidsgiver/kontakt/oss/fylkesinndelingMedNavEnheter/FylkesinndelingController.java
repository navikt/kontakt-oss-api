package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FylkesinndelingController {

    private final FylkesinndelingRepository fylkesinndelingRepository;

    public FylkesinndelingController(FylkesinndelingRepository fylkesinndelingRepository) {
        this.fylkesinndelingRepository = fylkesinndelingRepository;
    }

    @GetMapping(value = "/kommuner")
    public List<KommuneEllerBydel> hentKommuner() {
        return fylkesinndelingRepository.alleLokasjoner();
    }
}
