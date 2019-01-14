package no.nav.tag.kontaktskjema.uthenting;

import no.nav.tag.kontaktskjema.Kontaktskjema;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static no.nav.tag.kontaktskjema.TestData.lagKontaktskjema;
import static no.nav.tag.kontaktskjema.uthenting.UthentingUtils.MELDING;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UthentingUtilsTest {

    @Autowired
    private UthentingUtils uthentingUtils;

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

        assertEquals(uthentinger, uthentingUtils.lagSorterteUthentinger(kontaktskjemaer));
    }

    @Test
    public void skalSortereUthentingerPaaId() {
        Set<Kontaktskjema> usorterteKontaktskjemaer = new HashSet<>();
        Kontaktskjema kontaktskjema;
        for (int i=0; i<100; i++) {
            kontaktskjema = lagKontaktskjema();
            kontaktskjema.setId(i);
            usorterteKontaktskjemaer.add(kontaktskjema);
        }

        List<KontaktskjemaUthenting> uthentinger = uthentingUtils.lagSorterteUthentinger(usorterteKontaktskjemaer);
        List<KontaktskjemaUthenting> sorterteUthentinger = uthentinger
                .stream()
                .sorted(Comparator.comparing(KontaktskjemaUthenting::getId))
                .collect(Collectors.toList());
        assertEquals("Listen er ikke sortert.", sorterteUthentinger, uthentinger);
    }
}