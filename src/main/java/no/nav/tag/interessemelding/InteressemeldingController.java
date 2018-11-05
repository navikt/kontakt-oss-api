package no.nav.tag.interessemelding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class InteressemeldingController {

    private final InteressemeldingRepository repository;

    @Autowired
    public InteressemeldingController(InteressemeldingRepository repository) {
        this.repository = repository;
    }

    @PostMapping(value = "/meldInteresse")
    public ResponseEntity meldInteresse(
            @RequestBody Interessemelding interessemelding
    ) {
        repository.save(interessemelding);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @RequestMapping("/hentAlle")
    public Iterable<Interessemelding> hentAlle() {
        return repository.findAll();
    }

}