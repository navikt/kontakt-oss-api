package no.nav.tag.kontakt.oss.events;

import lombok.Value;
import no.nav.tag.kontakt.oss.Kontaktskjema;

@Value
public class BesvarelseMottatt {
    private final boolean suksess;
    private final Kontaktskjema kontaktskjema;
}
