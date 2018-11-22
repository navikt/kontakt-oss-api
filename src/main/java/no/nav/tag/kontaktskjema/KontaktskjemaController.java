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
        System.out.println(kontaktskjema);
        repository.save(kontaktskjema);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
