package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.*;

@ToString
@NoArgsConstructor
public class FylkesinndelingMedNavEnheter {

    private Map<String, List<KommuneEllerBydel>> fylkeTilKommuneEllerBydel;
    private Map<NavEnhet, NavFylkesenhet> navEnhetTilFylkesenhet;
    private Map<KommuneEllerBydel, NavEnhet> kommuneEllerBydelTilNavenhet;

    public FylkesinndelingMedNavEnheter(Map<String, List<KommuneEllerBydel>> fylkeTilKommuneEllerBydel) {
        this.fylkeTilKommuneEllerBydel = fylkeTilKommuneEllerBydel;
    }

    public FylkesinndelingMedNavEnheter(
            Map<NavEnhet, NavFylkesenhet> navEnhetTilFylkesenhet,
            Map<KommuneEllerBydel, NavEnhet> kommuneEllerBydelTilNavenhet,
            List<KommuneEllerBydel> kommunerOgBydeler
    ) {
        this.navEnhetTilFylkesenhet = navEnhetTilFylkesenhet;
        this.kommuneEllerBydelTilNavenhet = kommuneEllerBydelTilNavenhet;

        this.fylkeTilKommuneEllerBydel = new HashMap<>();

        for (KommuneEllerBydel kommuneEllerBydel : kommunerOgBydeler) {
            NavFylkesenhet fylkesenhet = finnTilhoerendeFylkesenhet(kommuneEllerBydel);
            if (fylkesenhet != null) {
                leggTilMappingMellom(kommuneEllerBydel, fylkesenhet);
            }
        }
    }

    private void leggTilMappingMellom(KommuneEllerBydel kommuneEllerBydel, NavFylkesenhet fylkesenhet) {
        String fylkesenhetsNr = fylkesenhet.getEnhetNr();
        boolean mapInneholderEnhet = this.fylkeTilKommuneEllerBydel.containsKey(fylkesenhetsNr);
        if (mapInneholderEnhet) {
            List<KommuneEllerBydel> fylketsKommunerOgBydeler = this.fylkeTilKommuneEllerBydel.get(fylkesenhetsNr);
            fylketsKommunerOgBydeler.add(kommuneEllerBydel);
        } else {
            List<KommuneEllerBydel> list = new ArrayList<>();
            list.add(kommuneEllerBydel);
            this.fylkeTilKommuneEllerBydel.put(
                    fylkesenhetsNr,
                    list
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

    public Map<String, List<KommuneEllerBydel>> getFylkeTilKommuneEllerBydel() {
        return fylkeTilKommuneEllerBydel;
    }

}
