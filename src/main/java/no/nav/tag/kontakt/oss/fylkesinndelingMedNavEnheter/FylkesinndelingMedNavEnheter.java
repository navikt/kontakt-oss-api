package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import lombok.NoArgsConstructor;

import java.util.*;

@NoArgsConstructor
public class FylkesinndelingMedNavEnheter {

    private Map<String, List<KommuneEllerBydel>> mapFraFylkesenheterTilKommunerOgBydeler;
    private Map<NavEnhet, NavFylkesenhet> navEnhetTilFylkesenhet;
    private Map<KommuneEllerBydel, NavEnhet> kommuneEllerBydelTilNavenhet;

    public FylkesinndelingMedNavEnheter(Map<String, List<KommuneEllerBydel>> mapFraFylkesenheterTilKommunerOgBydeler) {
        this.mapFraFylkesenheterTilKommunerOgBydeler = mapFraFylkesenheterTilKommunerOgBydeler;
    }

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
            if (fylkesenhet != null) {
                leggTilMappingMellom(kommuneEllerBydel, fylkesenhet);
            }
        }
    }

    private void leggTilMappingMellom(KommuneEllerBydel kommuneEllerBydel, NavFylkesenhet fylkesenhet) {
        String fylkesenhetsNr = fylkesenhet.getEnhetNr();
        if (this.mapFraFylkesenheterTilKommunerOgBydeler.containsKey(fylkesenhetsNr)) {
            this.mapFraFylkesenheterTilKommunerOgBydeler.get(fylkesenhetsNr).add(kommuneEllerBydel);
        } else {
            List<KommuneEllerBydel> list = new ArrayList<>();
            list.add(kommuneEllerBydel);
            this.mapFraFylkesenheterTilKommunerOgBydeler.put(
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

    public Map<String, List<KommuneEllerBydel>> getMapFraFylkesenheterTilKommunerOgBydeler() {
        return mapFraFylkesenheterTilKommunerOgBydeler;
    }

}
