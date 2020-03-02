package no.nav.tag.kontakt.oss;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    public ResponseEntity meldInteresse(@RequestBody Kontaktskjema kontaktskjema) {

        if (StringUtils.isNotEmpty(kontaktskjema.getFylke())
                && StringUtils.isEmpty(kontaktskjema.getFylkesenhetsnr())
        ) {
            kontaktskjema.setFylkesenhetsnr(kontaktskjema.getFylke());
        } else if (StringUtils.isNotEmpty(kontaktskjema.getFylkesenhetsnr())
                && StringUtils.isEmpty(kontaktskjema.getFylke())
        ) {
            kontaktskjema.setFylke(kontaktskjema.getFylkesenhetsnr());
        }

        if (kontaktskjemaService.harMottattForMangeInnsendinger()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        kontaktskjemaService.lagreKontaktskjemaOgSendTilSalesforce(kontaktskjema);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
