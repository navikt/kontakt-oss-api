package no.nav.arbeidsgiver.kontakt.oss.events;

import lombok.Value;
import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;

@Value
public class GsakOppgaveOpprettet {
    private final Integer gsakId;
    private final Kontaktskjema kontaktskjema;
}
