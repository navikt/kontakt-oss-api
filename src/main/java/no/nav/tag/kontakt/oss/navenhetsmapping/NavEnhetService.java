package no.nav.tag.kontakt.oss.navenhetsmapping;

import no.nav.tag.kontakt.oss.KontaktskjemaException;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.FylkesinndelingRepository;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.KommuneEllerBydel;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.NavEnhet;
import org.springframework.stereotype.Component;

@Component
public class NavEnhetService {

    private final FylkesinndelingRepository fylkesinndelingRepository;

    public NavEnhetService(FylkesinndelingRepository fylkesinndelingRepository) {
        this.fylkesinndelingRepository = fylkesinndelingRepository;
    }

    public String mapFraKommunenrTilEnhetsnr(String kommunenr) {
        NavEnhet navEnhet = fylkesinndelingRepository
                .hentKommuneNrEllerBydelNrTilNavEnhet()
                .get(kommunenr);
        if (navEnhet != null) {
                return navEnhet.getEnhetNr();
        } else {
            throw new KontaktskjemaException("Finner ingen NAV-enhet tilhørende kommune " + kommunenr);
        }
    }
}
