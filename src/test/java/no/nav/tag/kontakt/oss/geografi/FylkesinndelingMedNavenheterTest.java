package no.nav.tag.kontakt.oss.geografi;

import no.nav.tag.kontakt.oss.geografi.integrasjon.Bydel;
import no.nav.tag.kontakt.oss.geografi.integrasjon.Kommune;
import no.nav.tag.kontakt.oss.geografi.integrasjon.KommuneEllerBydel;
import no.nav.tag.kontakt.oss.geografi.integrasjon.NorgGeografi;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class FylkesinndelingMedNavenheterTest {

    /*
    TODO TAG-298 Disse testene er fremdeles relevante, men skal gjøres med fylkesenhet i stedet for geografiske fylker.
    @Test
    public void geografi__skal_handtere_flere_fylker() {
        List<NorgGeografi> norgGeografi = Arrays.asList(
                new NorgGeografi("01", "fylke1"),
                new NorgGeografi("0101", "kommune1.1"),
                new NorgGeografi("0102", "kommune1.2"),

                new NorgGeografi("02", "fylke2"),
                new NorgGeografi("0201", "kommune2.1"),
                new NorgGeografi("0202", "kommune2.2")

        );
        List<KommuneEllerBydel> kommunerTilhoerendeFylke1 = new FylkesinndelingMedNavenheter(norgGeografi).getGeografiMap().get("01");
        List<KommuneEllerBydel> kommunerTilhoerendeFylke2 = new FylkesinndelingMedNavenheter(norgGeografi).getGeografiMap().get("02");
        assertThat(kommunerTilhoerendeFylke1).contains(
                new Kommune("0101", "kommune1.1"),
                new Kommune("0102", "kommune1.2")
        );
        assertThat(kommunerTilhoerendeFylke2).contains(
                new Kommune("0201", "kommune2.1"),
                new Kommune("0202", "kommune2.2")
        );
        assertThat(kommunerTilhoerendeFylke1).hasSize(2);
        assertThat(kommunerTilhoerendeFylke2).hasSize(2);
    }

    @Test
    public void geografi__fylke_skal_ha_tilhoerende_kommuner_og_ikke_flere() {
        List<NorgGeografi> norgGeografi = Arrays.asList(
                new NorgGeografi("01", "et fylke"),
                new NorgGeografi("0101", "en kommune"),
                new NorgGeografi("0201", "en annen kommune")
        );
        List<KommuneEllerBydel> kommunerTilhoerendeFylke = new FylkesinndelingMedNavenheter(norgGeografi).getGeografiMap().get("01");
        assertThat(kommunerTilhoerendeFylke).isEqualTo(Collections.singletonList(new Kommune("0101", "en kommune")));
    }

    @Test
    public void geografi__fylke_skal_ha_tilhoerende_bydeler_og_ikke_flere() {
        List<NorgGeografi> norgGeografi = Arrays.asList(
                new NorgGeografi("01", "fylke"),
                new NorgGeografi("0101", "kommune"),
                new NorgGeografi("010101", "bydel1"),
                new NorgGeografi("020101", "bydel2")
        );
        List<KommuneEllerBydel> kommunerTilhoerendeFylke = new FylkesinndelingMedNavenheter(norgGeografi).getGeografiMap().get("01");
        assertThat(kommunerTilhoerendeFylke).contains(new Bydel("010101", "kommune–bydel1"));
        assertThat(kommunerTilhoerendeFylke).doesNotContain(new Bydel("020101", "kommune–bydel2"));
    }
    */

    @Test
    public void geografi__skal_ikke_ta_med_kommuner_med_null() {
        List<NorgGeografi> norgGeografi = Arrays.asList(
                new NorgGeografi("01", "et fylke"),
                new NorgGeografi("0101", null),
                new NorgGeografi(null, "blabla"),
                new NorgGeografi(null, null)
        );
        List<KommuneEllerBydel> kommunerOgBydeler = hentAlleKommunerOgBydeler(new FylkesinndelingMedNavenheter(norgGeografi));
        assertThat(kommunerOgBydeler).isEmpty();
    }

    @Test
    public void geografi__skal_ikke_ta_med_norgGeografier_med_bokstaver_i_navn() {
        List<NorgGeografi> norgGeografi = Arrays.asList(
                new NorgGeografi("01", "et fylke"),
                new NorgGeografi("0101a", "kommune")
        );
        List<KommuneEllerBydel> kommunerOgBydeler = hentAlleKommunerOgBydeler(new FylkesinndelingMedNavenheter(norgGeografi));
        assertThat(kommunerOgBydeler).isEmpty();
    }

    @Test
    public void geografi__skal_ikke_ta_med_norgGeografier_med_navn_av_ugyldig_lengde() {
        List<NorgGeografi> norgGeografi = Arrays.asList(
                new NorgGeografi("01", "et fylke"),
                new NorgGeografi("010", "kommune")
        );
        List<KommuneEllerBydel> kommunerOgBydeler = hentAlleKommunerOgBydeler(new FylkesinndelingMedNavenheter(norgGeografi));
        assertThat(kommunerOgBydeler).isEmpty();
    }

    @Test
    public void geografi__skal_ikke_legge_til_kommunen_hvis_den_har_bydeler() {
        List<NorgGeografi> norgGeografi = Arrays.asList(
                new NorgGeografi("01", "fylke"),
                new NorgGeografi("0101", "kommune"),
                new NorgGeografi("010101", "bydel")
        );
        List<KommuneEllerBydel> kommunerOgBydeler = hentAlleKommunerOgBydeler(new FylkesinndelingMedNavenheter(norgGeografi));
        assertThat(kommunerOgBydeler).doesNotContain(new Kommune("0101", "kommune"));
    }

    @Test
    public void geografi__skal_legge_til_bydeler_med_kommunenavn_lagt_paa() {
        List<NorgGeografi> norgGeografi = Arrays.asList(
                new NorgGeografi("01", "fylke"),
                new NorgGeografi("0101", "kommune"),
                new NorgGeografi("010101", "bydel1"),
                new NorgGeografi("010102", "bydel2")
        );
        List<KommuneEllerBydel> kommunerTilhoerendeFylke = new FylkesinndelingMedNavenheter(norgGeografi).getGeografiMap().get("alle");
        assertThat(kommunerTilhoerendeFylke).contains(
                new Bydel("010101", "kommune–bydel1"),
                new Bydel("010102", "kommune–bydel2")
        );
    }

    private List<KommuneEllerBydel> hentAlleKommunerOgBydeler(FylkesinndelingMedNavenheter geografi) {
        List<KommuneEllerBydel> alleKommunerOgBydeler = new ArrayList<>();
        geografi.getGeografiMap().forEach((fylkesnr, kommunerOgBydeler) -> alleKommunerOgBydeler.addAll(kommunerOgBydeler));
        return alleKommunerOgBydeler;
    }
}