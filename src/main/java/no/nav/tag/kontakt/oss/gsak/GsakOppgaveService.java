package no.nav.tag.kontakt.oss.gsak;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.DateProvider;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.featureToggles.FeatureToggles;
import no.nav.tag.kontakt.oss.metrics.Metrics;
import no.nav.tag.kontakt.oss.navenhetsmapping.NavEnhetUtils;
import no.nav.tag.kontakt.oss.gsak.GsakOppgave.OppgaveStatus;
import no.nav.tag.kontakt.oss.gsak.integrasjon.GsakKlient;
import no.nav.tag.kontakt.oss.gsak.integrasjon.GsakRequest;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static no.bekk.bekkopen.org.OrganisasjonsnummerValidator.isValid;
import static no.nav.tag.kontakt.oss.gsak.GsakOppgave.OppgaveStatus.*;

@Component
@Slf4j
public class GsakOppgaveService {

    private final GsakOppgaveRepository oppgaveRepository;
    private final DateProvider dateProvider;
    private final GsakKlient gsakKlient;
    private final NavEnhetUtils enhetUtils;
    private final FeatureToggles featureToggles;
    private final Metrics metrics;

    @Autowired
    public GsakOppgaveService(
            GsakOppgaveRepository oppgaveRepository,
            DateProvider dateProvider,
            GsakKlient gsakKlient,
            NavEnhetUtils enhetUtils,
            FeatureToggles featureToggles, Metrics metrics) {
        this.oppgaveRepository = oppgaveRepository;
        this.dateProvider = dateProvider;
        this.gsakKlient = gsakKlient;
        this.enhetUtils = enhetUtils;
        this.featureToggles = featureToggles;
        this.metrics = metrics;
    }

    @AllArgsConstructor
    private static class Behandlingsresultat {
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
        MDC.remove("correlationId");
    }

    private Behandlingsresultat opprettOppgaveIGsak(Kontaktskjema kontaktskjema) {
        if (!this.featureToggles.isEnabled("gsak")) {
            log.info("Opprettet ikke ny gsak-oppgave kontaktskjema {}, tjenesten er togglet av.", kontaktskjema.getId());
            return new Behandlingsresultat(DISABLED, null);
        }
        try {
            Integer gsakId = gsakKlient.opprettGsakOppgave(lagGsakInnsending(kontaktskjema));
            log.info("Opprettet ny gsak-oppgave med id {}", gsakId);
            metrics.sendtGsakOppgave(true);
            return new Behandlingsresultat(OK, gsakId);
        } catch (Exception e) {
            log.error("Opprettelse av gsak-oppgave feilet for kontaktskjema {}.", kontaktskjema.getId(), e);
            metrics.sendtGsakOppgave(false);
            return new Behandlingsresultat(FEILET, null);
        }
    }

    GsakRequest lagGsakInnsending(Kontaktskjema kontaktskjema) {
        String enhetsnr = enhetUtils.mapFraKommunenrTilEnhetsnr(kontaktskjema.getKommunenr());
        LocalDate aktivDato = dateProvider.now().toLocalDate();

        return new GsakRequest(
                enhetsnr,
                "9999",
                isValid(kontaktskjema.getBedriftsnr()) ? kontaktskjema.getBedriftsnr() : "",
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