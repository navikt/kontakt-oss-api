package no.nav.tag.kontaktskjema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KontaktskjemaController {

    private final KontaktskjemaRepository repository;

    @Autowired
    public KontaktskjemaController(KontaktskjemaRepository repository) {
        this.repository = repository;
    }

    @PostMapping(value = "/tag-kontaktskjema/meldInteresse")
    public ResponseEntity meldInteresse(
            @RequestBody Kontaktskjema kontaktskjema
    ) {
        System.out.println(kontaktskjema);
        repository.save(kontaktskjema);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
