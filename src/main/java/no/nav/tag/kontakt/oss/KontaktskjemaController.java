package no.nav.tag.kontakt.oss;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class KontaktskjemaController {
    private final KontaktskjemaService kontaktskjemaService;

    @Autowired
    public KontaktskjemaController(
            KontaktskjemaService kontaktskjemaService
    ) {
        this.kontaktskjemaService = kontaktskjemaService;
    }

    @Transactional
    @PostMapping(value = "/meldInteresse")
    public ResponseEntity meldInteresse(
            @RequestBody Kontaktskjema kontaktskjema
    ) {
        if (kontaktskjemaService.harMottattForMangeInnsendinger()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        kontaktskjemaService.lagreKontaktskjemaOgSendTilSalesforce(kontaktskjema);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
