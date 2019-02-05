package no.nav.tag.kontakt.oss.gsak;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.DateProvider;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.KontaktskjemaException;
import no.nav.tag.kontakt.oss.enhetsmapping.EnhetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import no.nav.tag.kontakt.oss.gsak.GsakOppgave.OppgaveStatus;

import java.time.LocalDate;

import static no.nav.tag.kontakt.oss.gsak.GsakOppgave.OppgaveStatus.*;

@NoArgsConstructor
@Component
@Slf4j
public class GsakOppgaveForSkjema {

    private GsakOppgaveRepository oppgaveRepository;
    private DateProvider dateProvider;
    private GsakKlient gsakKlient;
    private EnhetUtils enhetUtils;

    @Autowired
    public GsakOppgaveForSkjema(GsakOppgaveRepository oppgaveRepository, DateProvider dateProvider, GsakKlient gsakKlient, EnhetUtils enhetUtils) {
        this.oppgaveRepository = oppgaveRepository;
        this.dateProvider = dateProvider;
        this.gsakKlient = gsakKlient;
        this.enhetUtils = enhetUtils;
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
        String enhetsnr = enhetUtils.mapFraKommunenrTilEnhetsnr(kontaktskjema.getKommunenr());
        String beskrivelse = "Arbeidsgiver har sendt henvendelse gjennom Kontaktskjema, blabla.";
        LocalDate aktivDato = dateProvider.now().toLocalDate();

        GsakInnsending innsending = new GsakInnsending(
                enhetsnr,
                beskrivelse,
                "ARBD",
                "OPA",
                "VURD_HENV",
                "HOY",
                aktivDato,
                aktivDato.plusDays(2)
        );

        try {
            Integer gsakId = gsakKlient.opprettGsakOppgave(innsending);
            return new Behandlingsresultat(OK, gsakId);
        } catch (KontaktskjemaException e) {
            log.error("Opprettelse av gsak-oppgave feilet.", e);
            return new Behandlingsresultat(FEILET, null);
        }
        // return new Behandlingsresultat(DISABLED, null);
    }
}