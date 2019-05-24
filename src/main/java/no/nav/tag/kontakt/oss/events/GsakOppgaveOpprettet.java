package no.nav.tag.kontakt.oss.events;

import lombok.Value;
import no.nav.tag.kontakt.oss.Kontaktskjema;

@Value
public class GsakOppgaveOpprettet {
    private final Integer gsakId;
    private final Kontaktskjema kontaktskjema;
}
