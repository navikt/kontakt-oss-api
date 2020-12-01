package no.nav.arbeidsgiver.kontakt.oss.gsak;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import no.nav.arbeidsgiver.kontakt.oss.BadRequestException;
import no.nav.arbeidsgiver.kontakt.oss.DateProvider;
import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;
import no.nav.arbeidsgiver.kontakt.oss.TemaType;
import no.nav.arbeidsgiver.kontakt.oss.events.GsakOppgaveOpprettet;
import no.nav.arbeidsgiver.kontakt.oss.events.GsakOppgaveSendt;
import no.nav.arbeidsgiver.kontakt.oss.gsak.GsakOppgave.OppgaveStatus;
import no.nav.arbeidsgiver.kontakt.oss.gsak.integrasjon.GsakKlient;
import no.nav.arbeidsgiver.kontakt.oss.gsak.integrasjon.GsakRequest;
import no.nav.arbeidsgiver.kontakt.oss.navenhetsmapping.NavEnhetService;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static no.bekk.bekkopen.org.OrganisasjonsnummerValidator.isValid;
import static no.nav.arbeidsgiver.kontakt.oss.gsak.GsakOppgave.OppgaveStatus.*;

@Component
@Slf4j
public class GsakOppgaveService {

    private final GsakOppgaveRepository oppgaveRepository;
    private final DateProvider dateProvider;
    private final GsakKlient gsakKlient;
    private final NavEnhetService navEnhetService;
    private final ApplicationEventPublisher eventPublisher;

    static final String GSAK_TEMAGRUPPE_ARBEID = "ARBD";
    static final String GSAK_TEMA_OPPFØLGING_ARBEIDSGIVER = "OPA";
    static final String GSAK_TEMA_INKLUDERENDE_ARBEIDSLIV = "IAR";
    static final String CORRELATION_ID = "correlationId";

    public static final String FYLKESENHETNR_TIL_MØRE_OG_ROMSDAL = "1500";

    @Autowired
    public GsakOppgaveService(
            GsakOppgaveRepository oppgaveRepository,
            DateProvider dateProvider,
            GsakKlient gsakKlient,
            NavEnhetService navEnhetService,
            ApplicationEventPublisher eventPublisher) {
        this.oppgaveRepository = oppgaveRepository;
        this.dateProvider = dateProvider;
        this.gsakKlient = gsakKlient;
        this.navEnhetService = navEnhetService;
        this.eventPublisher = eventPublisher;
    }

    @Value
    public static class Behandlingsresultat {
        private final OppgaveStatus status;
        private final Integer gsakId;
    }

    @Transactional
    public void opprettOppgaveOgLagreStatus(Kontaktskjema kontaktskjema) {
        try {
            MDC.put(CORRELATION_ID, UUID.randomUUID().toString());

            if (FYLKESENHETNR_TIL_MØRE_OG_ROMSDAL.equals(kontaktskjema.getFylkesenhetsnr())) {
                GsakRequest gsakRequest = lagGsakInnsending(kontaktskjema);
                Behandlingsresultat behandlingsresultat = opprettOppgaveIGsak(gsakRequest, kontaktskjema);
                eventPublisher.publishEvent(new GsakOppgaveSendt(behandlingsresultat, gsakRequest));
                oppgaveRepository.save(new GsakOppgave.GsakOppgaveBuilder()
                        .kontaktskjemaId(kontaktskjema.getId())
                        .status(behandlingsresultat.status)
                        .opprettet(dateProvider.now())
                        .gsakId(behandlingsresultat.gsakId)
                        .build());
            } else {
                oppgaveRepository.save(new GsakOppgave.GsakOppgaveBuilder()
                        .kontaktskjemaId(kontaktskjema.getId())
                        .status(SKAL_IKKE_SENDES)
                        .opprettet(dateProvider.now())
                        .gsakId(null)
                        .build());
            }
        } finally {
            MDC.remove(CORRELATION_ID);
        }
    }

    private Behandlingsresultat opprettOppgaveIGsak(GsakRequest gsakRequest, Kontaktskjema kontaktskjema) {
        try {
            log.info("Oppretter ny gsak-oppgave for kontaktskjema {}", kontaktskjema.getId());
            Behandlingsresultat behandlingsresultat = sendInnGsakOppgaveOgProvPaNyttUtenOrgnrHvisBadRequest(gsakRequest);
            eventPublisher.publishEvent(new GsakOppgaveOpprettet(behandlingsresultat.gsakId, kontaktskjema));
            return behandlingsresultat;

        } catch (Exception e) {
            log.error("Opprettelse av gsak-oppgave feilet for kontaktskjema {}.", kontaktskjema.getId(), e);
            return new Behandlingsresultat(FEILET, null);
        }
    }

    private Behandlingsresultat sendInnGsakOppgaveOgProvPaNyttUtenOrgnrHvisBadRequest(GsakRequest gsakRequest) {
        try {
            return sendGsakRequest(gsakRequest);

        } catch (BadRequestException e) {
            // BadRequest kan tyde på at orgnr blir feilvalidert i GSAK.
            // Vi har forskjellig validering av orgnr enn GSAK.
            log.error(e.getMessage(), e);
            log.warn("Prøver å opprette GSAK oppgave igjen uten orgnr");
            gsakRequest.setOrgnr("");
            return sendGsakRequest(gsakRequest);
        }
    }

    private Behandlingsresultat sendGsakRequest(GsakRequest gsakRequest) {
        return new Behandlingsresultat(OK, gsakKlient.opprettGsakOppgave(gsakRequest));
    }

    GsakRequest lagGsakInnsending(Kontaktskjema kontaktskjema) {
        String enhetsnr;
        LocalDate aktivDato = dateProvider.now().toLocalDate();
        String temagruppe = null;
        String tema;
        String beskrivelse;

        if (TemaType.FOREBYGGE_SYKEFRAVÆR.equals(kontaktskjema.getTemaType())) {
            enhetsnr = navEnhetService.mapFraFylkesenhetNrTilArbeidslivssenterEnhetsnr(kontaktskjema.getFylkesenhetsnr());
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
                        "Kontakt arbeidsgiver for å avklare hva henvendelsen gjelder.",
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
