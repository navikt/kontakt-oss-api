package no.nav.arbeidsgiver.kontakt.oss.events;

import lombok.Value;
import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;

@Value
public class BesvarelseMottatt {
    private final boolean suksess;
    private final Kontaktskjema kontaktskjema;
}
