package no.nav.tag.kontakt.oss;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.metrics.Metrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@Slf4j
public class KontaktskjemaController {

    static final int MAX_INNSENDINGER_PR_TI_MIN = 10;
    private final KontaktskjemaRepository repository;
    private final Metrics metrics;

    @Autowired
    public KontaktskjemaController(KontaktskjemaRepository repository, Metrics metrics) {
        this.repository = repository;
        this.metrics = metrics;
    }

    @PostMapping(value = "${controller.basepath}/meldInteresse")
    public ResponseEntity meldInteresse(
            @RequestBody Kontaktskjema kontaktskjema
    ) {
        try {
            kontaktskjema.setOpprettet(LocalDateTime.now());
            if(repository.findAllNewerThan(LocalDateTime.now().minusMinutes(10)).size() >= MAX_INNSENDINGER_PR_TI_MIN) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
            }
            repository.save(kontaktskjema);
            log.info("Vellykket innsending.");
            metrics.mottattKontaktskjema(true);

            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Feil ved innsending av skjema", e);
            metrics.mottattKontaktskjema(false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
