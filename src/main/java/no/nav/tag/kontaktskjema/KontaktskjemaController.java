package no.nav.tag.kontaktskjema;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"https://tjenester.nav.no", "https://tjenester-q1.nav.no", "https://tjenester-t1.nav.no"})
@RestController
@Slf4j
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
            kontaktskjema.setOpprettet(LocalDateTime.now());
            repository.save(kontaktskjema);
            log.info("Vellykket innsending.");
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Feil ved innsending av skjema", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
