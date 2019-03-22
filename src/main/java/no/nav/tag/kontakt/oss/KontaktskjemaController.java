package no.nav.tag.kontakt.oss;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.metrics.Metrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@Slf4j
public class KontaktskjemaController {

    private final KontaktskjemaRepository repository;
    private final Metrics metrics;
    private final int maksInnsendingerPerTiMin;

    @Autowired
    public KontaktskjemaController(
            KontaktskjemaRepository repository,
            Metrics metrics,
            @Value("${kontaktskjema.max-requests-per-10-min}") Integer maksInnsendingerPerTiMin
    ) {
        this.repository = repository;
        this.metrics = metrics;
        this.maksInnsendingerPerTiMin = maksInnsendingerPerTiMin;
    }

    @PostMapping(value = "/meldInteresse")
    public ResponseEntity meldInteresse(
            @RequestBody Kontaktskjema kontaktskjema
    ) {
        try {
            kontaktskjema.setOpprettet(LocalDateTime.now());
            if(repository.findAllNewerThan(LocalDateTime.now().minusMinutes(10)).size() >= maksInnsendingerPerTiMin) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
            }
            repository.save(kontaktskjema);
            log.info("Vellykket innsending.");
            metrics.mottattKontaktskjema(true, kontaktskjema);

            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Feil ved innsending av skjema", e);
            metrics.mottattKontaktskjema(false, kontaktskjema);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
