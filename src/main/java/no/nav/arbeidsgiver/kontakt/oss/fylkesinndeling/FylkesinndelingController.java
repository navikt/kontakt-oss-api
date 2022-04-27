package no.nav.arbeidsgiver.kontakt.oss.fylkesinndeling;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class FylkesinndelingController {
    private final FylkesinndelingService kommuneService;

    @GetMapping(value = "/kommuner")
    public List<KommuneEllerBydel> hentKommuner() {
        return kommuneService.hentFylkesinndelinger();
    }
}
