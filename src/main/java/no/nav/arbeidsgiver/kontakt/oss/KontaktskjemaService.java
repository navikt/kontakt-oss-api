package no.nav.arbeidsgiver.kontakt.oss;

import lombok.extern.slf4j.Slf4j;
import no.nav.arbeidsgiver.kontakt.oss.events.BesvarelseMottatt;
import no.nav.arbeidsgiver.kontakt.oss.navenhetsmapping.NavEnhetService;
import no.nav.arbeidsgiver.kontakt.oss.salesforce.utsending.KontaktskjemaUtsending;
import no.nav.arbeidsgiver.kontakt.oss.salesforce.utsending.KontaktskjemaUtsendingRepository;
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
    private final KontaktskjemaRepository kontaktskjemaRepository;
    private final KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final DateProvider dateProvider;
    private final NavEnhetService navEnhetService;

    @SuppressWarnings("RegExpRedundantEscape") // brukes i character class, må escapes
    private final static String LATIN = "a-zA-Z ?\\-–'._)(/";
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
            KontaktskjemaRepository kontaktskjemaRepository,
            KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository,
            ApplicationEventPublisher eventPublisher,
            DateProvider dateProvider,
            NavEnhetService navEnhetService
    ) {
        this.maksInnsendingerPerTiMin = maksInnsendingerPerTiMin;
        this.kontaktskjemaRepository = kontaktskjemaRepository;
        this.kontaktskjemaUtsendingRepository = kontaktskjemaUtsendingRepository;
        this.eventPublisher = eventPublisher;
        this.dateProvider = dateProvider;
        this.navEnhetService = navEnhetService;
    }

    public void lagreKontaktskjemaOgSendTilSalesforce(Kontaktskjema kontaktskjema) {
        try {
            validerKontaktskjema(kontaktskjema);
            kontaktskjema.setOpprettet(dateProvider.now());
            Kontaktskjema lagretKontaktskjema = kontaktskjemaRepository.save(kontaktskjema);
            kontaktskjemaUtsendingRepository.save(
                    KontaktskjemaUtsending.klarTilUtsending(
                            lagretKontaktskjema.getId(),
                            dateProvider.now()
                    )
            );
        } catch (Exception e) {
            eventPublisher.publishEvent(new BesvarelseMottatt(false, kontaktskjema));
            throw e;
        }
        eventPublisher.publishEvent(new BesvarelseMottatt(true, kontaktskjema));
    }

    private void validerKontaktskjema(Kontaktskjema kontaktskjema) {
        try {
            if (TemaType.FOREBYGGE_SYKEFRAVÆR.equals(kontaktskjema.getTemaType())) {
                navEnhetService.mapFraFylkesenhetNrTilArbeidslivssenterEnhetsnr(kontaktskjema.getFylkesenhetsnr());
            } else {
                navEnhetService.mapFraKommunenrTilEnhetsnr(kontaktskjema.getKommunenr());
            }
        } catch (KontaktskjemaException e) {
            log.warn("Feil ved validering av kontaktskjema", e);
            throw new BadRequestException("Innsendt kontaktskjema er ugyldig");
        }

        validerFelter(kontaktskjema);
    }

    private void validerFelter(Kontaktskjema kontaktskjema) {
        validerSkjemafelt(kontaktskjema.getBedriftsnavn(), RAUS_TEKST);
        validerSkjemafelt(kontaktskjema.getKommune(), RAUS_TEKST);
        validerSkjemafelt(kontaktskjema.getTelefonnr(), SIFRE_MELLOMROM_OG_PLUSS);
        validerSkjemafelt(kontaktskjema.getEpost(), EPOST);
        validerKommuneOgFylke(kontaktskjema);
        validerNavn(kontaktskjema);
        validerOrgnr(kontaktskjema.getOrgnr());
    }

    private void validerKommuneOgFylke(Kontaktskjema kontaktskjema){
        if(TemaType.REKRUTTERING.equals(kontaktskjema.getTemaType())){
            validerKommune(kontaktskjema);
        }else{
            validerFylke(kontaktskjema);
        }

    }
    private void validerFylke(Kontaktskjema kontaktskjema) {
        validerSkjemafelt(kontaktskjema.getFylkesenhetsnr(), RAUS_TEKST);
    }
    private void validerKommune(Kontaktskjema kontaktskjema) {
        validerSkjemafelt(kontaktskjema.getKommune(), RAUS_TEKST);
        validerSkjemafelt(kontaktskjema.getTelefonnr(), SIFRE_MELLOMROM_OG_PLUSS);
    }

    private void validerNavn(Kontaktskjema kontaktskjema) {
        if (kontaktskjema.getFornavn().isPresent() && kontaktskjema.getEtternavn().isPresent()) {
            validerSkjemafelt(kontaktskjema.getFornavn().get(), RAUS_TEKST);
            validerSkjemafelt(kontaktskjema.getEtternavn().get(), RAUS_TEKST);
        }else if(kontaktskjema.getNavn().isPresent() ){
            validerSkjemafelt(kontaktskjema.getNavn().get(),RAUS_TEKST);
        }
        else{
            String feil = "Navn eller fornavn eller etternavn må være utfylt";
            throw new BadRequestException(feil);
        }
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
        return kontaktskjemaRepository.findAllNewerThan(LocalDateTime.now().minusMinutes(10)).size() >= maksInnsendingerPerTiMin;
    }
}
