package no.nav.tag.kontaktskjema.uthenting;

import no.nav.tag.kontaktskjema.Kontaktskjema;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.*;

import static no.nav.tag.kontaktskjema.uthenting.UthentingUtils.MELDING;
import static org.junit.Assert.*;

public class UthentingUtilsTest {

    @Test
    public void skalOversetteKontaktskjemaTilRiktigUthenting() {
        List<String> mottakere = Arrays.asList("Lisa@nav.no", "Bernt@nav.no");
        Map<String, List<String>> epostliste = Collections.singletonMap("0011", mottakere);

        UthentingUtils uthentingUtils = new UthentingUtils(epostliste);

        Kontaktskjema kontaktskjema = new Kontaktskjema(
                734,
                LocalDateTime.now(),
                "dette er en melding",
                "nordland",
                "Bodø",
                "0011",
                "Flesk og Fisk AS",
                "Ola",
                "Nordmann",
                "ola.nordmann@fleskOgFisk.no",
                "01234567"
        );

        KontaktskjemaUthenting uthenting = new KontaktskjemaUthenting(
                734,
                kontaktskjema.getOpprettet(),
                MELDING,
                mottakere,
                "nordland",
                "Bodø",
                "0011",
                "Flesk og Fisk AS",
                "Ola",
                "Nordmann",
                "ola.nordmann@fleskOgFisk.no",
                "01234567"
        );

        List<KontaktskjemaUthenting> uthentinger = Arrays.asList(uthenting, uthenting, uthenting);
        List<Kontaktskjema> kontaktskjemaer = Arrays.asList(kontaktskjema, kontaktskjema, kontaktskjema);

        assertEquals(uthentinger, uthentingUtils.lagUthentinger(kontaktskjemaer));
    }
}