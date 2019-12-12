package no.nav.tag.kontakt.oss;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.events.BesvarelseMottatt;
import no.nav.tag.kontakt.oss.navenhetsmapping.NavEnhetService;
import no.nav.tag.kontakt.oss.salesforce.SalesforceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

import static no.bekk.bekkopen.org.OrganisasjonsnummerValidator.isValid;

@Slf4j
@Service
public class KontaktskjemaService {
    private final int maksInnsendingerPerTiMin;
    private final KontaktskjemaRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final DateProvider dateProvider;
    private final NavEnhetService navEnhetService;
    private final SalesforceService salesforceService;

    private final static String LATIN = "a-zA-Z \\-–'._)(/";
    private final static String SAMISK = "ÁáČčĐđŊŋŠšŦŧŽž";
    private final static String NORSK = "æøåÆØÅ";

    private final static String VANLIGE_BOKSTAVER = LATIN + SAMISK + NORSK;
    private final static String SIFRE = "0-9";
    private final static String EPOSTTEGN = "\\.@+";
    private final static String AKSENTER = "ëÿüïöäéúíóáèùìòàêûîôâõãñËŸÜÏÖÄÉÚÍÓÁÈÙÌÒÀÊÛÎÔÂÕÃÑ";

    private final static Pattern RAUS_TEKST = Pattern.compile("^[" + VANLIGE_BOKSTAVER + SIFRE + AKSENTER + "]*$");
    private final static Pattern EPOST = Pattern.compile("^[" + VANLIGE_BOKSTAVER + SIFRE + AKSENTER + EPOSTTEGN + "]*$");
    private final static Pattern SIFRE_MELLOMROM_OG_PLUSS = Pattern.compile("^[" + SIFRE + "+ " + "]*$");


    public KontaktskjemaService(
            @Value("${kontaktskjema.max-requests-per-10-min}") Integer maksInnsendingerPerTiMin,
            KontaktskjemaRepository repository,
            ApplicationEventPublisher eventPublisher,
            DateProvider dateProvider,
            NavEnhetService navEnhetService,
            SalesforceService salesforceService
    ) {
        this.maksInnsendingerPerTiMin = maksInnsendingerPerTiMin;
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.dateProvider = dateProvider;
        this.navEnhetService = navEnhetService;
        this.salesforceService = salesforceService;
    }

    public void lagreKontaktskjemaOgSendTilSalesforce(Kontaktskjema kontaktskjema) {
        try {
            validerKontaktskjema(kontaktskjema);
            kontaktskjema.setOpprettet(dateProvider.now());
            repository.save(kontaktskjema);
            salesforceService.sendKontaktskjemaTilSalesforce(kontaktskjema);
        } catch (Exception e) {
            eventPublisher.publishEvent(new BesvarelseMottatt(false, kontaktskjema));
            log.error("Feil ved lagring av kontaktskjema", e);
            throw e;
        }
        eventPublisher.publishEvent(new BesvarelseMottatt(true, kontaktskjema));
    }

    private void validerKontaktskjema(Kontaktskjema kontaktskjema) {
        try {
            if (TemaType.FOREBYGGE_SYKEFRAVÆR.equals(kontaktskjema.getTemaType())) {
                navEnhetService.mapFraFylkesenhetNrTilArbeidslivssenterEnhetsnr(kontaktskjema.getFylke());
            } else {
                navEnhetService.mapFraKommunenrTilEnhetsnr(kontaktskjema.getKommunenr());
            }
        } catch (KontaktskjemaException e) {
            throw new BadRequestException("Innsendt kontaktskjema er ugyldig", e);
        }

        validerFelter(kontaktskjema);
    }

    private void validerFelter(Kontaktskjema kontaktskjema) {
        validerSkjemafelt(kontaktskjema.getBedriftsnavn(), RAUS_TEKST);
        validerSkjemafelt(kontaktskjema.getFornavn(), RAUS_TEKST);
        validerSkjemafelt(kontaktskjema.getEtternavn(), RAUS_TEKST);
        validerSkjemafelt(kontaktskjema.getFylke(), RAUS_TEKST);
        validerSkjemafelt(kontaktskjema.getKommune(), RAUS_TEKST);
        validerSkjemafelt(kontaktskjema.getTelefonnr(), SIFRE_MELLOMROM_OG_PLUSS);
        validerSkjemafelt(kontaktskjema.getEpost(), EPOST);
        validerOrgnr(kontaktskjema.getOrgnr());
    }

    private void validerSkjemafelt(String felt, Pattern skalBareInneholde) {
        if (!skalBareInneholde.matcher(felt).matches()) {
            String feil = "Skjemafelt \"" + felt + "\" må passe med det regulære uttrykket \"" + skalBareInneholde.pattern() + "\"";

            log.error(feil);
            throw new BadRequestException(feil);
        }
    }

    private void validerOrgnr(String orgnr) {
        if (orgnr == null || "".equals(orgnr)) {
            return;
        }
        if (!isValid(orgnr)) {
            throw new BadRequestException("Orgnr " + orgnr + " er ugyldig");
        }
    }

    public boolean harMottattForMangeInnsendinger() {
        return repository.findAllNewerThan(LocalDateTime.now().minusMinutes(10)).size() >= maksInnsendingerPerTiMin;
    }
}
