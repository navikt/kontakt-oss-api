package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FylkesinndelingMedNavEnheter {
    private final NorgService norgService;

    @Autowired
    public FylkesinndelingMedNavEnheter(NorgService norgService) {
        this.norgService = norgService;
    }
}
