package no.nav.tag.kontakt.oss.gsak;

import static no.nav.tag.kontakt.oss.gsak.GsakOppgave.OppgaveStatus.DISABLED;

import no.nav.tag.kontakt.oss.DateProvider;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import no.nav.tag.kontakt.oss.gsak.GsakOppgave.OppgaveStatus;

@Component
public class GsakOppgaveForSkjema {

    @AllArgsConstructor
    private class Behandlingsresultat {
        private OppgaveStatus status;
        private Integer gsakId;
    }

    @Autowired
    GsakOppgaveRepository oppgaveRepository;

    @Autowired
    DateProvider dateProvider;

    @Transactional
    public void opprettOppgaveOgLagreStatus(Kontaktskjema kontaktskjema) {
        Behandlingsresultat behandlingsresultat = opprettOppgaveIGsak(kontaktskjema);
        oppgaveRepository.save(new GsakOppgave.GsakOppgaveBuilder()
                .kontaktskjemaId(kontaktskjema.getId())
                .status(behandlingsresultat.status)
                .opprettet(dateProvider.now())
                .gsakId(behandlingsresultat.gsakId)
                .build());
    }

    private Behandlingsresultat opprettOppgaveIGsak(Kontaktskjema kontaktskjema) {
        // TODO Implementere reelt kall mot gsak med støtte for OK og FEIL
        return new Behandlingsresultat(DISABLED, null);
    }


}
