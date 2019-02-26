package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FylkesinndelingMedNavEnheter {

    private Map<NavFylkesenhet, List<KommuneEllerBydel>> mapFraFylkesenheterTilKommunerOgBydeler;
    private Map<NavEnhet, NavFylkesenhet> navEnhetTilFylkesenhet;
    private Map<KommuneEllerBydel, NavEnhet> kommuneEllerBydelTilNavenhet;

    public FylkesinndelingMedNavEnheter(
            Map<NavEnhet, NavFylkesenhet> navEnhetTilFylkesenhet,
            Map<KommuneEllerBydel, NavEnhet> kommuneEllerBydelTilNavenhet,
            List<KommuneEllerBydel> kommunerOgBydeler
    ) {
        this.navEnhetTilFylkesenhet = navEnhetTilFylkesenhet;
        this.kommuneEllerBydelTilNavenhet = kommuneEllerBydelTilNavenhet;

        this.mapFraFylkesenheterTilKommunerOgBydeler = new HashMap<>();

        for (KommuneEllerBydel kommuneEllerBydel : kommunerOgBydeler) {
            NavFylkesenhet fylkesenhet = finnTilhoerendeFylkesenhet(kommuneEllerBydel);
            leggTilMappingMellom(kommuneEllerBydel, fylkesenhet);
        }
    }

    private void leggTilMappingMellom(KommuneEllerBydel kommuneEllerBydel, NavFylkesenhet fylkesenhet) {
        if (this.mapFraFylkesenheterTilKommunerOgBydeler.containsKey(fylkesenhet)) {
            this.mapFraFylkesenheterTilKommunerOgBydeler.get(fylkesenhet).add(kommuneEllerBydel);
        } else {
            this.mapFraFylkesenheterTilKommunerOgBydeler.put(
                    fylkesenhet,
                    Collections.singletonList(kommuneEllerBydel)
            );
        }
    }

    private NavFylkesenhet finnTilhoerendeFylkesenhet(KommuneEllerBydel kommuneEllerBydel) {
        if (!kommuneEllerBydelTilNavenhet.containsKey(kommuneEllerBydel)) {
            return null;
        }
        NavEnhet navEnhet = kommuneEllerBydelTilNavenhet.get(kommuneEllerBydel);
        if (!navEnhetTilFylkesenhet.containsKey(navEnhet)) {
            return null;
        }
        return navEnhetTilFylkesenhet.get(navEnhet);
    }

    public Map<NavEnhet, NavFylkesenhet> getNavEnhetTilFylkesenhet() {
        return navEnhetTilFylkesenhet;
    }

}
