package no.nav.tag.kontakt.oss.gsak;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.DateProvider;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.enhetsmapping.EnhetUtils;
import no.nav.tag.kontakt.oss.gsak.GsakOppgave.OppgaveStatus;
import no.nav.tag.kontakt.oss.gsak.integrasjon.GsakKlient;
import no.nav.tag.kontakt.oss.gsak.integrasjon.GsakRequest;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static no.nav.tag.kontakt.oss.gsak.GsakOppgave.OppgaveStatus.FEILET;
import static no.nav.tag.kontakt.oss.gsak.GsakOppgave.OppgaveStatus.OK;

@Component
@Slf4j
public class GsakOppgaveService {

    private final GsakOppgaveRepository oppgaveRepository;
    private final DateProvider dateProvider;
    private final GsakKlient gsakKlient;
    private final EnhetUtils enhetUtils;

    @Autowired
    public GsakOppgaveService(GsakOppgaveRepository oppgaveRepository, DateProvider dateProvider, GsakKlient gsakKlient, EnhetUtils enhetUtils) {
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
        MDC.put("correlationId", UUID.randomUUID().toString());
        Behandlingsresultat behandlingsresultat = opprettOppgaveIGsak(kontaktskjema);
        oppgaveRepository.save(new GsakOppgave.GsakOppgaveBuilder()
                .kontaktskjemaId(kontaktskjema.getId())
                .status(behandlingsresultat.status)
                .opprettet(dateProvider.now())
                .gsakId(behandlingsresultat.gsakId)
                .build());
        MDC.clear();
    }

    private Behandlingsresultat opprettOppgaveIGsak(Kontaktskjema kontaktskjema) {
        try {
            Integer gsakId = gsakKlient.opprettGsakOppgave(lagGsakInnsending(kontaktskjema));
            return new Behandlingsresultat(OK, gsakId);
        } catch (Exception e) {
            log.error("Opprettelse av gsak-oppgave feilet for kontaktskjema {}.", kontaktskjema.getId(), e);
            return new Behandlingsresultat(FEILET, null);
        }
    }

    private GsakRequest lagGsakInnsending(Kontaktskjema kontaktskjema) {
        String enhetsnr = enhetUtils.mapFraKommunenrTilEnhetsnr(kontaktskjema.getKommunenr());
        LocalDate aktivDato = dateProvider.now().toLocalDate();

        return new GsakRequest(
                enhetsnr,
                "9999",
                kontaktskjema.getBedriftsnr(),
                lagBeskrivelse(kontaktskjema),
                "ARBD",
                "OPA",
                "VURD_HENV",
                "HOY",
                aktivDato.toString(),
                aktivDato.plusDays(2).toString()
        );
    }

    private String lagBeskrivelse(Kontaktskjema kontaktskjema) {
        return String.format(
                "Kontaktskjema: Arbeidsgiver har sendt henvendelse gjennom Kontaktskjema; \n" +
                        "Tema: %s \n" +
                        "Navn: %s \n" +
                        "Nummer: %s \n" +
                        "E-post: %s \n" +
                        "Kommune: %s (kommunenr: %s) \n" +
                        "Kontakt arbeidsgiver for å avklare hva henvendelsen gjelder. Husk å registrere henvendelsen som aktivitetstype «Kontaktskjema» i Arena.",
                kontaktskjema.getTema(),
                kontaktskjema.getFornavn() + " " + kontaktskjema.getEtternavn(),
                kontaktskjema.getTelefonnr(),
                kontaktskjema.getEpost(),
                kontaktskjema.getKommune(),
                kontaktskjema.getKommunenr()
        );

    }
}