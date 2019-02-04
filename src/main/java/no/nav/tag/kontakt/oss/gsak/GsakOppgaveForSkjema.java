package no.nav.tag.kontakt.oss.gsak;

import static no.nav.tag.kontakt.oss.gsak.GsakOppgave.OppgaveStatus.DISABLED;

import lombok.NoArgsConstructor;
import no.nav.tag.kontakt.oss.DateProvider;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import no.nav.tag.kontakt.oss.gsak.GsakOppgave.OppgaveStatus;

@NoArgsConstructor
@Component
public class GsakOppgaveForSkjema {

    private GsakOppgaveRepository oppgaveRepository;
    private DateProvider dateProvider;
    private GsakKlient gsakKlient;

    @Autowired
    public GsakOppgaveForSkjema(GsakOppgaveRepository oppgaveRepository, DateProvider dateProvider, GsakKlient gsakKlient) {
        this.oppgaveRepository = oppgaveRepository;
        this.dateProvider = dateProvider;
        this.gsakKlient = gsakKlient;
    }

    @AllArgsConstructor
    private class Behandlingsresultat {
        private OppgaveStatus status;
        private Integer gsakId;
    }

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
        // gsakKlient.opprett();
        return new Behandlingsresultat(DISABLED, null);
    }


}
