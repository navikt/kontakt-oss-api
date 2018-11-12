package no.nav.tag.kontaktskjema;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KontaktskjemaController {

    @PostMapping(value = "/tag-kontaktskjema/meldInteresse")
    public ResponseEntity meldInteresse(
            @RequestBody Skjema skjema
    ) {
        System.out.println(skjema);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
