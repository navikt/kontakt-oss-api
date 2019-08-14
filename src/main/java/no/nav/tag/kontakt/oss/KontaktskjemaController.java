package no.nav.tag.kontakt.oss;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

import static no.bekk.bekkopen.org.OrganisasjonsnummerValidator.isValid;

@Slf4j
@RestController
public class KontaktskjemaController {
    private final KontaktskjemaService kontaktskjemaService;

    private final static String LATIN = "a-zA-Z \\-–'._";
    private final static String SAMISK = "ÁáČčĐđŊŋŠšŦŧŽž";
    private final static String NORSK = "æøåÆØÅ";

    private final static String VANLIGE_BOKSTAVER = LATIN + SAMISK + NORSK;
    private final static String SIFRE = "0-9";
    private final static String EPOSTTEGN = "\\.@+";
    private final static String AKSENTER = "ëÿüïöäéúíóáèùìòàêûîôâõãñËŸÜÏÖÄÉÚÍÓÁÈÙÌÒÀÊÛÎÔÂÕÃÑ";

    private final static Pattern RAUS_TEKST = Pattern.compile("^[" + VANLIGE_BOKSTAVER + SIFRE + AKSENTER + "]*$");
    private final static Pattern EPOST = Pattern.compile("^[" + VANLIGE_BOKSTAVER + SIFRE + AKSENTER + EPOSTTEGN + "]*$");
    private final static Pattern SIFRE_MELLOMROM_OG_PLUSS = Pattern.compile("^[" + SIFRE + "+ " + "]*$");

    @Autowired
    public KontaktskjemaController(
            KontaktskjemaService kontaktskjemaService
    ) {
        this.kontaktskjemaService = kontaktskjemaService;
    }

    @PostMapping(value = "/meldInteresse")
    public ResponseEntity meldInteresse(
            @RequestBody Kontaktskjema kontaktskjema
    ) {
        if (kontaktskjemaService.harMottattForMangeInnsendinger()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        validerKontaktskjema(kontaktskjema);
        kontaktskjemaService.lagreKontaktskjema(kontaktskjema);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private void validerKontaktskjema(Kontaktskjema kontaktskjema) {
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
        if (!isValid(orgnr)) {
            throw new BadRequestException("Orgnr " + orgnr + " er ugyldig");
        }
    }
}
