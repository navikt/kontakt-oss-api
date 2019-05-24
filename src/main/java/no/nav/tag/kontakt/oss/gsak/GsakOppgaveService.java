package no.nav.tag.kontakt.oss.gsak;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.DateProvider;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.TemaType;
import no.nav.tag.kontakt.oss.events.GsakOppgaveOpprettet;
import no.nav.tag.kontakt.oss.events.GsakOppgaveSendt;
import no.nav.tag.kontakt.oss.featureToggles.FeatureToggles;
import no.nav.tag.kontakt.oss.gsak.integrasjon.BadRequestException;
import no.nav.tag.kontakt.oss.navenhetsmapping.NavEnhetService;
import no.nav.tag.kontakt.oss.gsak.GsakOppgave.OppgaveStatus;
import no.nav.tag.kontakt.oss.gsak.integrasjon.GsakKlient;
import no.nav.tag.kontakt.oss.gsak.integrasjon.GsakRequest;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
    private final NavEnhetService navEnhetService;
    private final FeatureToggles featureToggles;
    private final ApplicationEventPublisher eventPublisher;

    public static final String GSAK_TEMAGRUPPE_ARBEID = "ARBD";
    public static final String GSAK_TEMA_OPPFØLGING_ARBEIDSGIVER = "OPA";
    public static final String GSAK_TEMA_INKLUDERENDE_ARBEIDSLIV = "IAR";

    @Autowired
    public GsakOppgaveService(
            GsakOppgaveRepository oppgaveRepository,
            DateProvider dateProvider,
            GsakKlient gsakKlient,
            NavEnhetService navEnhetService,
            FeatureToggles featureToggles,
            ApplicationEventPublisher eventPublisher) {
        this.oppgaveRepository = oppgaveRepository;
        this.dateProvider = dateProvider;
        this.gsakKlient = gsakKlient;
        this.navEnhetService = navEnhetService;
        this.featureToggles = featureToggles;
        this.eventPublisher = eventPublisher;
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
            log.info("Opprettet ikke ny gsak-oppgave for kontaktskjema {}, tjenesten er togglet av.", kontaktskjema.getId());
            return new Behandlingsresultat(DISABLED, null);
        }

        try {
            return sendInnGsakOppgaveOgProvPaNyttUtenOrgnrHvisBadRequest(kontaktskjema);

        } catch (Exception e) {
            log.error("Opprettelse av gsak-oppgave feilet for kontaktskjema {}.", kontaktskjema.getId(), e);
            eventPublisher.publishEvent(new GsakOppgaveSendt(false));
            return new Behandlingsresultat(FEILET, null);
        }
    }

    private Behandlingsresultat sendInnGsakOppgaveOgProvPaNyttUtenOrgnrHvisBadRequest(Kontaktskjema kontaktskjema) {
        try {
            return sendGsakRequest(kontaktskjema);

        } catch (BadRequestException e) {
            // BadRequest kan tyde på at orgnr blir feilvalidert i GSAK.
            // Vi har forskjellig validering av orgnr enn GSAK.
            log.error(e.getMessage(), e);
            log.warn("Prøver å opprette GSAK oppgave igjen uten orgnr for kontaktskjema med id: {}", kontaktskjema.getId());
            kontaktskjema.setOrgnr("");
            return sendGsakRequest(kontaktskjema);
        }
    }


    private Behandlingsresultat sendGsakRequest(Kontaktskjema kontaktskjema) {
        Integer gsakId = gsakKlient.opprettGsakOppgave(lagGsakInnsending(kontaktskjema));
        log.info("Opprettet ny gsak-oppgave med id {}", gsakId);
        eventPublisher.publishEvent(new GsakOppgaveSendt(true));
        eventPublisher.publishEvent(new GsakOppgaveOpprettet(gsakId, kontaktskjema));
        return new Behandlingsresultat(OK, gsakId);
    }

    GsakRequest lagGsakInnsending(Kontaktskjema kontaktskjema) {
        String enhetsnr;
        LocalDate aktivDato = dateProvider.now().toLocalDate();
        String temagruppe = null;
        String tema;
        String beskrivelse;

        if (TemaType.FOREBYGGE_SYKEFRAVÆR.equals(kontaktskjema.getTemaType())) {
            enhetsnr = navEnhetService.mapFraFylkesenhetNrTilArbeidslivssenterEnhetsnr(kontaktskjema.getFylke());
            tema = GSAK_TEMA_INKLUDERENDE_ARBEIDSLIV;
            beskrivelse = lagBeskrivelseForHenvendelseOmSykefravær(kontaktskjema);
        } else {
            enhetsnr = navEnhetService.mapFraKommunenrTilEnhetsnr(kontaktskjema.getKommunenr());
            temagruppe = GSAK_TEMAGRUPPE_ARBEID; // TODO Undersøk om denne kan fjernes
            tema = GSAK_TEMA_OPPFØLGING_ARBEIDSGIVER;
            beskrivelse = lagBeskrivelse(kontaktskjema);
        }

        return new GsakRequest(
                enhetsnr,
                "9999",
                isValid(kontaktskjema.getOrgnr()) ? kontaktskjema.getOrgnr() : "",
                beskrivelse,
                temagruppe,
                tema,
                "VURD_HENV",
                "HOY",
                aktivDato.toString(),
                aktivDato.plusDays(2).toString()
        );
    }

    private String lagBeskrivelseForHenvendelseOmSykefravær(Kontaktskjema kontaktskjema) {
        String harSnakketMedAnsattrepresentant = kontaktskjema.getHarSnakketMedAnsattrepresentant() ? "Ja" : "Nei";

        return String.format(
                "Kontaktskjema: Arbeidsgiver har sendt henvendelse gjennom Kontaktskjema; \n" +
                        "Tema: %s \n" +
                        "Snakket med tillitsvalgt eller ansattrepresentant: %s \n" +
                        "Bedriftsnavn: %s \n" +
                        "Navn: %s \n" +
                        "Telefonnr: %s \n" +
                        "E-post: %s \n" +
                        "Kommune: %s (kommunenr: %s) \n" +
                        "Kontakt arbeidsgiver for å avklare hva henvendelsen gjelder. Husk å registrere henvendelsen som aktivitetstype «Kontaktskjema» i Arena.",
                kontaktskjema.getTema(),
                harSnakketMedAnsattrepresentant,
                kontaktskjema.getBedriftsnavn(),
                kontaktskjema.getFornavn() + " " + kontaktskjema.getEtternavn(),
                kontaktskjema.getTelefonnr(),
                kontaktskjema.getEpost(),
                kontaktskjema.getKommune(),
                kontaktskjema.getKommunenr()
        );
    }

    private String lagBeskrivelse(Kontaktskjema kontaktskjema) {
        return String.format(
                "Kontaktskjema: Arbeidsgiver har sendt henvendelse gjennom Kontaktskjema; \n" +
                        "Tema: %s \n" +
                        "Bedriftsnavn: %s \n" +
                        "Navn: %s \n" +
                        "Telefonnr: %s \n" +
                        "E-post: %s \n" +
                        "Kommune: %s (kommunenr: %s) \n" +
                        "Kontakt arbeidsgiver for å avklare hva henvendelsen gjelder. Husk å registrere henvendelsen som aktivitetstype «Kontaktskjema» i Arena.",
                kontaktskjema.getTema(),
                kontaktskjema.getBedriftsnavn(),
                kontaktskjema.getFornavn() + " " + kontaktskjema.getEtternavn(),
                kontaktskjema.getTelefonnr(),
                kontaktskjema.getEpost(),
                kontaktskjema.getKommune(),
                kontaktskjema.getKommunenr()
        );

    }
}
