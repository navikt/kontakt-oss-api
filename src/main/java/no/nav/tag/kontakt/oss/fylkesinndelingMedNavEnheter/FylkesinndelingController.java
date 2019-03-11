package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.KodeverkKlient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class FylkesinndelingController {

    private final FylkesinndelingRepository fylkesinndelingRepository;
    private final KodeverkKlient kodeverkKlient;

    public FylkesinndelingController(FylkesinndelingRepository fylkesinndelingRepository, KodeverkKlient kodeverkKlient) {
        this.fylkesinndelingRepository = fylkesinndelingRepository;
        this.kodeverkKlient = kodeverkKlient;
    }

    @GetMapping(value = "/fylkerOgKommuner")
    public Map<String, List<KommuneEllerBydel>> hentFylkerOgKommuner() {
        return fylkesinndelingRepository.hentFylkesinndeling().getFylkeTilKommuneEllerBydel();
    }

    @GetMapping(value = "/test")
    public Object test() {
        return kodeverkKlient.hentKommuner();
    }
}
