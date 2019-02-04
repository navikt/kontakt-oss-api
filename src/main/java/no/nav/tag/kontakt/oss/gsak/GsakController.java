package no.nav.tag.kontakt.oss.gsak;

import no.nav.tag.kontakt.oss.DateProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO TAG-233 Skal fjernes, er bare for å teste gsak-apiet direkte
@RestController
public class GsakController {

    private final GsakKlient gsakKlient;

    @Autowired
    public GsakController(GsakKlient gsakKlient) {
        this.gsakKlient = gsakKlient;
    }

    @GetMapping(value = "${controller.basepath}/gsak")
    public ResponseEntity gsak() {
        GsakInnsending innsending = new GsakInnsending(
                "0315",
                "blabla beskrivelse",
                "ARBD",
                "OPA",
                "VURD_HENV",
                "HOY",
                new DateProvider().now().toString(),
                new DateProvider().now().plusHours(48).toString()
        );
        Integer gsakId = gsakKlient.opprettGsakOppgave(innsending);
        return ResponseEntity.ok(gsakId);
    }
}
