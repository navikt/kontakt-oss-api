package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FylkesinndelingMedNavEnheter {
    private FylkesinndelingMedNavEnheter() {

    }

    public static Map<String, List<KommuneEllerBydel>> beregnFylkesinndeling(
            Map<NavEnhet, NavFylkesenhet> navEnhetTilFylkesenhet,
            Map<KommuneEllerBydel, NavEnhet> kommuneEllerBydelTilNavenhet,
            List<KommuneEllerBydel> kommunerOgBydeler
    ) {
        Map<String, List<KommuneEllerBydel>> fylkeTilKommuneEllerBydel = new HashMap<>();

        for (KommuneEllerBydel kommuneEllerBydel : kommunerOgBydeler) {
            NavEnhet navEnhet = kommuneEllerBydelTilNavenhet.get(kommuneEllerBydel);
            if (navEnhet == null) {
                continue;
            }

            NavFylkesenhet fylkesenhet = navEnhetTilFylkesenhet.get(navEnhet);
            if (fylkesenhet == null) {
                continue;
            }

            String fylkesenhetnr = fylkesenhet.getEnhetNr();
            if (fylkesenhetnr == null) {
                continue;
            }

            fylkeTilKommuneEllerBydel
                    .computeIfAbsent(fylkesenhetnr, _fylkesenhetnr -> new ArrayList<>())
                    .add(kommuneEllerBydel);
        }

        return fylkeTilKommuneEllerBydel;
    }
}
