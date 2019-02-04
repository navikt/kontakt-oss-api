package no.nav.tag.kontakt.oss.gsak;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// TODO TAG-233 Skal fjernes, er bare for å teste gsak-apiet direkte
@RestController
public class GsakController {

    private final GsakKlient gsakKlient;

    @Autowired
    public GsakController(GsakKlient gsakKlient) {
        this.gsakKlient = gsakKlient;
    }

    @PostMapping(value = "${controller.basepath}/gsak")
    public ResponseEntity gsak(
            @RequestBody GsakInnsending gsakInnsending
    ) {
        gsakKlient.opprettGsakOppgave(gsakInnsending);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
