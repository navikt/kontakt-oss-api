package no.nav.tag.interessemelding;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class InteressemeldingController {

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping(value = "/meldInteresse")
    public ResponseEntity meldInteresse(
            @RequestBody Interessemelding interessemelding
    ) {
        System.out.println(interessemelding);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}