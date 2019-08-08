package no.nav.tag.kontakt.oss.events;

import lombok.Value;
import no.nav.tag.kontakt.oss.gsak.GsakOppgaveService.Behandlingsresultat;
import no.nav.tag.kontakt.oss.gsak.integrasjon.GsakRequest;

@Value
public class GsakOppgaveSendt {
    private final Behandlingsresultat behandlingsresultat;
    private final GsakRequest gsakRequest;
}
