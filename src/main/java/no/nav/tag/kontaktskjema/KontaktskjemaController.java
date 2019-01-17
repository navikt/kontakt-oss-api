package no.nav.tag.kontaktskjema;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@CrossOrigin(origins = {"https://tjenester.nav.no", "https://tjenester-q1.nav.no", "https://tjenester-t1.nav.no"})
@RestController
@Slf4j
public class KontaktskjemaController {

    static final int MAX_INNSENDINGER_PR_TI_MIN = 10;
    private final KontaktskjemaRepository repository;

    @Autowired
    public KontaktskjemaController(KontaktskjemaRepository repository) {
        this.repository = repository;
    }

    @PostMapping(value = "${controller.basepath}/meldInteresse")
    public ResponseEntity meldInteresse(
            @RequestBody Kontaktskjema kontaktskjema
    ) {
        if (kontaktskjema.getId() != null) {
            throw new KontaktskjemaException("Innsendt kontaktskjema skal ikke ha satt id.");
        }
        try {
            kontaktskjema.setOpprettet(LocalDateTime.now());
            if(repository.findAllNewerThan(LocalDateTime.now().minusMinutes(10)).size() >= MAX_INNSENDINGER_PR_TI_MIN) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
            };
            repository.save(kontaktskjema);
            log.info("Vellykket innsending.");
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Feil ved innsending av skjema", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
