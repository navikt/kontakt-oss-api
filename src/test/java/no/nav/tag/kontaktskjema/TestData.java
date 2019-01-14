package no.nav.tag.kontaktskjema;

import java.time.LocalDateTime;

public class TestData {
    public static Kontaktskjema lagKontaktskjema() {
        return new Kontaktskjema(
                null,
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
    }
}
