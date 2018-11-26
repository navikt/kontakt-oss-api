package no.nav.tag.kontaktskjema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"https://tjenester.nav.no", "https://tjenester-q1.nav.no", "https://tjenester-t1.nav.no"})
@RestController
public class KontaktskjemaController {

    private final KontaktskjemaRepository repository;

    @Autowired
    public KontaktskjemaController(KontaktskjemaRepository repository) {
        this.repository = repository;
    }

    @PostMapping(value = "/kontaktskjema/meldInteresse")
    public ResponseEntity meldInteresse(
            @RequestBody Kontaktskjema kontaktskjema
    ) {
        kontaktskjema.setMelding(genererMelding(kontaktskjema));
        repository.save(kontaktskjema);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private String genererMelding(Kontaktskjema kontaktskjema) {
        return String.format("Emnefelt; Kontaktskjema Inkludering\n" +
                "Arbeidsgiver har sendt henvendelse gjennom Kontaktskjema Inkludering;\n" +
                "Navn: %s\n" +
                "Nummer: %s %s\n" +
                "E-post: %s\n" +
                "Kommune: %s\n" +
                "Kontakt arbeidsgiver for å   avklare hva henvendelsen gjelder.\n" +
                "Minner om at arbeidsgiver skal kontaktes innen 48 timer.\n" +
                "Husk å registrere henvendelsen som «Kontaktskjema Inkludering» i arena (ikke telefonkontakt)\n" +
                "Når arbeidsgiver er kontaktet og henvendelsen registrert i Arena skal denne eposten slettes.\n",
                kontaktskjema.getFornavn(), kontaktskjema.getEtternavn(), kontaktskjema.getTelefonnr(), kontaktskjema.getEpost(), kontaktskjema.getKommune()
        );

    }
}
