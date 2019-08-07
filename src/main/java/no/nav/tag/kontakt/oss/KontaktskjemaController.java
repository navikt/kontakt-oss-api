package no.nav.tag.kontakt.oss;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.regex.Pattern;

@Slf4j
@RestController
public class KontaktskjemaController {
    private final KontaktskjemaService kontaktskjemaService;

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

        if (!erGyldigKontaktskjema(kontaktskjema)) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        kontaktskjemaService.lagreKontaktskjema(kontaktskjema);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private boolean erGyldigKontaktskjema(Kontaktskjema kontaktskjema) {
        String latin = "a-zA-Z \\-'";
        String norsk = "æøåÆØÅ";
        String sifre = "0-9";
        String eposttegn = "\\.@+";
        String aksenter = "ëÿüïöäéúíóáèùìòàêûîôâõãñËŸÜÏÖÄÉÚÍÓÁÈÙÌÒÀÊÛÎÔÂÕÃÑ";

        String rausTekst = latin + norsk + sifre + aksenter;
        String epost = latin + norsk + sifre + aksenter + eposttegn;

        return erGyldigFelt(kontaktskjema.getBedriftsnavn(), rausTekst) &&
                erGyldigFelt(kontaktskjema.getFornavn(), rausTekst) &&
                erGyldigFelt(kontaktskjema.getEtternavn(), rausTekst) &&
                erGyldigFelt(kontaktskjema.getFylke(), rausTekst) &&
                erGyldigFelt(kontaktskjema.getKommune(), rausTekst) &&
                erGyldigFelt(kontaktskjema.getOrgnr(), sifre + " ") &&
                erGyldigFelt(kontaktskjema.getTelefonnr(), sifre + "+ ") &&
                erGyldigFelt(kontaktskjema.getEpost(), epost);
    }

    private boolean erGyldigFelt(String felt, String skalBareInneholde) {
        Pattern pattern = Pattern.compile("^[" + skalBareInneholde + "]*$");
        if (pattern.matcher(felt).matches()) {
            return true;
        } else {
            log.error("Skjemafelt \"" + felt + "\" må bare inneholde \"" + skalBareInneholde + "\"");
            return false;
        }
    }
}
