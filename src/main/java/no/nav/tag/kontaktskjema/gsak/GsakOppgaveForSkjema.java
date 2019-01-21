package no.nav.tag.kontaktskjema.gsak;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import no.nav.tag.kontaktskjema.Kontaktskjema;
import no.nav.tag.kontaktskjema.gsak.GsakOppgave.OppgaveStatus;

public class GsakOppgaveForSkjema {

    @Autowired
    GsakOppgaveRepository oppgaveRepository;

    @Transactional
    public void opprettOppgaveOgLagreStatus(Kontaktskjema lagKontaktskjema) {
        oppgaveRepository.save(new GsakOppgave.GsakOppgaveBuilder()
                .kontaktskjemaId(lagKontaktskjema.getId())
                .status(OppgaveStatus.DISABLED)
                .build());
    }


}
