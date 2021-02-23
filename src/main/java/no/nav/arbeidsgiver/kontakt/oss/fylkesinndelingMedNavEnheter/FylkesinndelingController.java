package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class FylkesinndelingController {

    private final FylkesinndelingRepository fylkesinndelingRepository;

    public FylkesinndelingController(FylkesinndelingRepository fylkesinndelingRepository) {
        this.fylkesinndelingRepository = fylkesinndelingRepository;
    }

    @GetMapping(value = "/fylkerOgKommuner")
    public Map<String, List<KommuneEllerBydel>> hentFylkerOgKommuner() {
        return fylkesinndelingRepository.hentFylkesinndeling().getFylkeTilKommuneEllerBydel();
    }
    @GetMapping(value = "/kommuner")
    public List<KommuneEllerBydel> hentKommuner() {
        return  fylkesinndelingRepository.hentFylkesinndeling().
                getFylkeTilKommuneEllerBydel()
                .values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

}
