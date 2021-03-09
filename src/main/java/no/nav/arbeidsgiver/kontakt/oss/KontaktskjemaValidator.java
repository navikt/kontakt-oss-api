package no.nav.arbeidsgiver.kontakt.oss;

import lombok.extern.slf4j.Slf4j;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.LokasjonsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static no.bekk.bekkopen.org.OrganisasjonsnummerValidator.isValid;

@Component
@Slf4j
public class KontaktskjemaValidator {
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

    private final LokasjonsValidator lokasjonsValidator;

    @Autowired
    public KontaktskjemaValidator(LokasjonsValidator lokasjonsValidator) {
        this.lokasjonsValidator = lokasjonsValidator;
    }

    void valider(Kontaktskjema kontaktskjema) {
        try {
            validerTemaTypeOgLokasjon(kontaktskjema);
            validerSkjemafelt(kontaktskjema.getBedriftsnavn(), RAUS_TEKST);
            validerSkjemafelt(kontaktskjema.getTelefonnr(), SIFRE_MELLOMROM_OG_PLUSS);
            validerSkjemafelt(kontaktskjema.getEpost(), EPOST);
            validerSkjemafelt(kontaktskjema.getNavn(), RAUS_TEKST);
            validerOrgnr(kontaktskjema.getOrgnr());
        } catch (Exception e) {
            log.error("Feil ved validering av kontaktskjema", e);
            throw new BadRequestException("Innsendt kontaktskjema er ugyldig");
        }
    }

    private void validerTemaTypeOgLokasjon(Kontaktskjema kontaktskjema) {
        switch (kontaktskjema.getTemaType()) {
            case REKRUTTERING:
                validerSkjemafelt(kontaktskjema.getKommune(), RAUS_TEKST);
                validerSkjemafelt(kontaktskjema.getTelefonnr(), SIFRE_MELLOMROM_OG_PLUSS);
                lokasjonsValidator.validerKommunenr(kontaktskjema.getKommunenr());
                break;

            case FOREBYGGE_SYKEFRAVÆR:
                validerSkjemafelt(kontaktskjema.getFylkesenhetsnr(), RAUS_TEKST);
                lokasjonsValidator.validerFylkesenhetnr(kontaktskjema.getFylkesenhetsnr());
                break;

            default:
                throw new BadRequestException("Ukjent tematype " + kontaktskjema.getTemaType());
        }
    }

    private void validerSkjemafelt(String felt, Pattern skalBareInneholde) {
        if (!skalBareInneholde.matcher(felt).matches()) {
            throw new BadRequestException(String.format(
                    "Skjemafelt \"%s\" må passe med det regulære uttrykket \"%s\"",
                    felt,
                    skalBareInneholde.pattern()
            ));
        }
    }

    private void validerOrgnr(String orgnr) {
        if (orgnr == null || orgnr.equals("") || isValid(orgnr)) {
            return;
        }

        throw new BadRequestException(String.format("Orgnr %s er ugyldig", orgnr));
    }
}
