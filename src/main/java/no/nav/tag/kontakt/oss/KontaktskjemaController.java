package no.nav.tag.kontakt.oss;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.events.BesvarelseMottatt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@Slf4j
public class KontaktskjemaController {

    private final KontaktskjemaService kontaktskjemaService;

    @Autowired
    public KontaktskjemaController(
            KontaktskjemaService kontaktskjemaService
    ) {
        this.kontaktskjemaService = kontaktskjemaService;
    }

    @PostMapping(value = "/meldInteresse")
    public ResponseEntity meldInteresse(
            @RequestBody Kontaktskjema kontaktskjema
    ) {
        if (kontaktskjemaService.harMottattForMangeInnsendinger()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        kontaktskjemaService.lagreKontaktskjema(kontaktskjema);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
