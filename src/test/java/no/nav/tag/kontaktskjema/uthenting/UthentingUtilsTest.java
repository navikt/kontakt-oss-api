package no.nav.tag.kontaktskjema.uthenting;

import no.nav.tag.kontaktskjema.Kontaktskjema;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_MAP;
import static no.nav.tag.kontaktskjema.uthenting.UthentingUtils.MELDING;
import static org.junit.Assert.assertEquals;

public class UthentingUtilsTest {

    @Test
    public void skalOversetteKontaktskjemaTilRiktigUthenting() {
        List<String> mottakere = Arrays.asList("Lisa@nav.no", "Bernt@nav.no");
        Map<String, List<String>> epostliste = Collections.singletonMap("0011", mottakere);

        UthentingUtils uthentingUtils = new UthentingUtils(epostliste);

        Kontaktskjema kontaktskjema = new Kontaktskjema(
                734,
                LocalDateTime.now(),
                "nordland",
                "Bodø",
                "0011",
                "Flesk og Fisk AS",
                "123456789",
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
                "123456789",
                "Ola",
                "Nordmann",
                "ola.nordmann@fleskOgFisk.no",
                "01234567"
        );

        List<KontaktskjemaUthenting> uthentinger = Arrays.asList(uthenting, uthenting, uthenting);
        List<Kontaktskjema> kontaktskjemaer = Arrays.asList(kontaktskjema, kontaktskjema, kontaktskjema);

        assertEquals(uthentinger, uthentingUtils.lagSorterteUthentinger(kontaktskjemaer));
    }

    @Test
    public void skalSortereUthentingerPaaId() {
        List<Kontaktskjema> usorterteKontaktskjemaer = new ArrayList<>();
        usorterteKontaktskjemaer.add(Kontaktskjema.builder().id(4).build());
        usorterteKontaktskjemaer.add(Kontaktskjema.builder().id(1).build());
        usorterteKontaktskjemaer.add(Kontaktskjema.builder().id(3).build());
        usorterteKontaktskjemaer.add(Kontaktskjema.builder().id(2).build());
        usorterteKontaktskjemaer.add(Kontaktskjema.builder().id(5).build());
        usorterteKontaktskjemaer.add(Kontaktskjema.builder().id(0).build());

        List<KontaktskjemaUthenting> uthentinger = new UthentingUtils(EMPTY_MAP).lagSorterteUthentinger(usorterteKontaktskjemaer);
        List<Integer> ider = uthentinger.stream()
                .map(KontaktskjemaUthenting::getId)
                .collect(Collectors.toList());
        assertEquals("Listen er ikke sortert.", Arrays.asList(0, 1, 2, 3, 4, 5), ider);
    }
}