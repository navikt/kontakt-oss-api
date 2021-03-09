package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class FylkesinndelingController {
    private final FylkesinndelingRepository fylkesinndelingRepository;

    @GetMapping(value = "/kommuner")
    public List<KommuneEllerBydel> hentKommuner() {
        return fylkesinndelingRepository.alleLokasjoner();
    }
}
