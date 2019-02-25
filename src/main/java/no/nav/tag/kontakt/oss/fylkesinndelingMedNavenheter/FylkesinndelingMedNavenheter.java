package no.nav.tag.kontakt.oss.fylkesinndelingMedNavenheter;

import no.nav.tag.kontakt.oss.fylkesinndelingMedNavenheter.integrasjon.NorgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FylkesinndelingMedNavenheter {
    private final NorgService norgService;

    @Autowired
    public FylkesinndelingMedNavenheter(NorgService norgService) {
        this.norgService = norgService;
    }
}
