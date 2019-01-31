package no.nav.tag.kontakt.oss;

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
                "123456789",
                "Ola",
                "Nordmann",
                "ola.nordmann@fleskOgFisk.no",
                "01234567",
                "Rekruttering"
        );
    }
}
