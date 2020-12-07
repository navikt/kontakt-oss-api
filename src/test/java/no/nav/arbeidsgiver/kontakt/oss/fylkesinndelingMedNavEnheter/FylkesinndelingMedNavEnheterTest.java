package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class FylkesinndelingMedNavEnheterTest {

    private FylkesinndelingMedNavEnheter fylkesinndeling;

    private static final Kommune kommune_1 = new Kommune("1", "1");
    private static final Kommune kommune_2 = new Kommune("2", "2");

    private static final Bydel bydel_1 = new Bydel("1", "1");
    private static final Bydel bydel_2 = new Bydel("2", "2");

    private static final NavEnhet navEnhet_1 = new NavEnhet("1");
    private static final NavEnhet navEnhet_2 = new NavEnhet("2");

    private static final NavFylkesenhet fylkesenhet_1 = new NavFylkesenhet("1");
    private static final NavFylkesenhet fylkesenhet_2 = new NavFylkesenhet("2");

    @Test
    public void fylkesinndeling__skal_sette_kommune_og_bydel_under_tilhoerende_fylkesenhet() {
        fylkesinndeling = new FylkesinndelingMedNavEnheter(
                Collections.singletonMap(navEnhet_1, fylkesenhet_1),
                Collections.singletonMap(kommune_1, navEnhet_1),
                Collections.singletonList(kommune_1)
        );

        Assertions.assertThat(fylkesinndeling.getFylkeTilKommuneEllerBydel().get(fylkesenhet_1.getEnhetNr())).contains(kommune_1);
    }

    @Test
    public void fylkesinndeling__skal_handtere_flere_fylker() {
        fylkesinndeling = new FylkesinndelingMedNavEnheter(
                new HashMap<>() {{
                    put(navEnhet_1, fylkesenhet_1);
                    put(navEnhet_2, fylkesenhet_2);
                }},
                new HashMap<>() {{
                    put(kommune_1, navEnhet_1);
                    put(kommune_2, navEnhet_2);
                }},
                Arrays.asList(kommune_1, kommune_2)
        );

        Map<String, List<KommuneEllerBydel>> resultat = fylkesinndeling.getFylkeTilKommuneEllerBydel();

        Assertions.assertThat(resultat.get(fylkesenhet_1.getEnhetNr())).isEqualTo(Collections.singletonList(kommune_1));
        Assertions.assertThat(resultat.get(fylkesenhet_2.getEnhetNr())).isEqualTo(Collections.singletonList(kommune_2));
    }

    @Test
    public void fylkesinndeling__fylke_skal_kunne_ha_flere_kommuner_og_bydeler() {
        fylkesinndeling = new FylkesinndelingMedNavEnheter(
                new HashMap<>() {{
                    put(navEnhet_1, fylkesenhet_1);
                }},
                new HashMap<>() {{
                    put(kommune_1, navEnhet_1);
                    put(kommune_2, navEnhet_1);
                    put(bydel_1, navEnhet_1);
                    put(bydel_2, navEnhet_1);
                }},
                Arrays.asList(kommune_1, kommune_2, bydel_1, bydel_2)
        );

        Map<String, List<KommuneEllerBydel>> resultat = fylkesinndeling.getFylkeTilKommuneEllerBydel();

        Assertions.assertThat(resultat.get(fylkesenhet_1.getEnhetNr())).isEqualTo(Arrays.asList(kommune_1, kommune_2, bydel_1, bydel_2));
    }

    @Test
    public void fylkesinndeling__kommuner_og_bydeler_som_ikke_har_navEnhet_skal_ikke_med() {
        fylkesinndeling = new FylkesinndelingMedNavEnheter(
                new HashMap<>(),
                new HashMap<>(),
                Arrays.asList(kommune_1, kommune_2, bydel_1, bydel_2)
        );

        Assertions.assertThat(hentAlleKommunerOgBydeler()).doesNotContain(kommune_1, kommune_2, bydel_1, bydel_2);
    }

    @Test
    public void fylkesinndeling__kommuner_som_har_navEnhet_som_ikke_har_fylkesEnhet_skal_ikke_med() {
        fylkesinndeling = new FylkesinndelingMedNavEnheter(
                new HashMap<>(),
                new HashMap<>() {{
                    put(kommune_1, navEnhet_1);
                    put(kommune_2, navEnhet_2);
                    put(bydel_1, navEnhet_1);
                    put(bydel_2, navEnhet_2);
                }},
                Arrays.asList(kommune_1, kommune_2, bydel_1, bydel_2)
        );

        Assertions.assertThat(hentAlleKommunerOgBydeler()).doesNotContain(kommune_1, kommune_2, bydel_1, bydel_2);
    }

    private List<KommuneEllerBydel> hentAlleKommunerOgBydeler() {
        return fylkesinndeling
                .getFylkeTilKommuneEllerBydel()
                .values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
