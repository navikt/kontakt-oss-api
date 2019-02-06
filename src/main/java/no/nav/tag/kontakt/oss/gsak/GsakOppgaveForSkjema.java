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
        try {
            Integer gsakId = gsakKlient.opprettGsakOppgave(lagGsakInnsending(kontaktskjema));
            return new Behandlingsresultat(OK, gsakId);
        } catch (KontaktskjemaException e) {
            log.error("Opprettelse av gsak-oppgave feilet.", e);
            return new Behandlingsresultat(FEILET, null);
        }
    }

    private GsakInnsending lagGsakInnsending(Kontaktskjema kontaktskjema) {
        String enhetsnr = enhetUtils.mapFraKommunenrTilEnhetsnr(kontaktskjema.getKommunenr());

        String beskrivelse = String.format(
                "Arbeidsgiver har sendt henvendelse gjennom Kontaktskjema; \n" +
                "Navn: %s \n" +
                "Nummer: %s \n" +
                "E-post: %s \n" +
                "Kommune: %s (kommunenr: %s) \n" +
                "Kontakt arbeidsgiver for å avklare hva henvendelsen gjelder. Husk å registrere henvendelsen som aktivitetstype «Kontaktskjema» i Arena.",
                kontaktskjema.getFornavn() + " " + kontaktskjema.getEtternavn(),
                kontaktskjema.getTelefonnr(),
                kontaktskjema.getEpost(),
                kontaktskjema.getKommune(),
                kontaktskjema.getKommunenr()
        );

        LocalDate aktivDato = dateProvider.now().toLocalDate();

        return new GsakInnsending(
                enhetsnr,
                beskrivelse,
                "ARBD",
                "OPA",
                "VURD_HENV",
                "HOY",
                aktivDato.toString(),
                aktivDato.plusDays(2).toString()
        );
    }
}