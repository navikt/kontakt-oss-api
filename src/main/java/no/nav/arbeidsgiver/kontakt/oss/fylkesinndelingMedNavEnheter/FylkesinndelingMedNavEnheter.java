package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FylkesinndelingMedNavEnheter {

    @Getter
    private Map<String, List<KommuneEllerBydel>> fylkeTilKommuneEllerBydel;

    public FylkesinndelingMedNavEnheter(
            Map<NavEnhet, NavFylkesenhet> navEnhetTilFylkesenhet,
            Map<KommuneEllerBydel, NavEnhet> kommuneEllerBydelTilNavenhet,
            List<KommuneEllerBydel> kommunerOgBydeler
    ) {
        this.fylkeTilKommuneEllerBydel = new HashMap<>();

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
    }
}
