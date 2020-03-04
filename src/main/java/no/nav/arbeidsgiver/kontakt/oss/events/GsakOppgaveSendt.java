package no.nav.arbeidsgiver.kontakt.oss.events;

import lombok.Value;
import no.nav.arbeidsgiver.kontakt.oss.gsak.GsakOppgaveService.Behandlingsresultat;
import no.nav.arbeidsgiver.kontakt.oss.gsak.integrasjon.GsakRequest;

@Value
public class GsakOppgaveSendt {
    private final Behandlingsresultat behandlingsresultat;
    private final GsakRequest gsakRequest;
}
