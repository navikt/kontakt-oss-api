package no.nav.tag.kontaktskjema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@ConditionalOnProperty(prefix = "feature.toggle", name = "uthentingsendepunkt", havingValue="disabled")
@CrossOrigin(origins = {"https://tjenester.nav.no", "https://tjenester-q1.nav.no", "https://tjenester-t1.nav.no"})
@RestController
public class KontaktskjemaController {

    private final KontaktskjemaRepository repository;

    @Autowired
    public KontaktskjemaController(KontaktskjemaRepository repository) {
        this.repository = repository;
    }

    @PostMapping(value = "${controller.basepath}/meldInteresse")
    public ResponseEntity meldInteresse(
            @RequestBody Kontaktskjema kontaktskjema
    ) {
        try {
            kontaktskjema.setMelding(genererMelding(kontaktskjema));
            repository.save(kontaktskjema);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String genererMelding(Kontaktskjema kontaktskjema) {
        return String.format("Emnefelt; Kontaktskjema Inkludering " +
                "Arbeidsgiver har sendt henvendelse gjennom Kontaktskjema Inkludering; " +
                "Navn: %s %s " +
                "Nummer: %s " +
                "E-post: %s " +
                "Kommune: %s " +
                "Kontakt arbeidsgiver for å avklare hva henvendelsen gjelder. " +
                "Minner om at arbeidsgiver skal kontaktes innen 48 timer. " +
                "Husk å registrere henvendelsen som «Kontaktskjema Inkludering» i arena (ikke telefonkontakt) " +
                "Når arbeidsgiver er kontaktet og henvendelsen registrert i Arena skal denne eposten slettes.",
                kontaktskjema.getFornavn(), kontaktskjema.getEtternavn(), kontaktskjema.getTelefonnr(), kontaktskjema.getEpost(), kontaktskjema.getKommune()
        );

    }
}
