package no.nav.tag.kontakt.oss;

import no.nav.tag.kontakt.oss.gsak.integrasjon.GsakKlient;
import no.nav.tag.kontakt.oss.gsak.integrasjon.GsakRequest;
import org.slf4j.MDC;
import org.springframework.http.*;

import java.time.LocalDateTime;

public class TestData {
    public static Kontaktskjema lagKontaktskjema() {
        return new Kontaktskjema(
                null,
                LocalDateTime.now(),
                "nordland",
                "Bodø",
                "0011",
                "Flesk og Fisk AS",
                "123456789",
                "Ola",
                "Nordmann",
                "ola.nordmann@fleskOgFisk.no",
                "01234567",
                "Rekruttering"
        );
    }

    public static GsakRequest lagGsakRequest() {
        return new GsakRequest(
                "0000",
                "beskrivelse",
                "ARBD",
                "OPA",
                "VURD_HENV",
                "HOY",
                "1970-10-10",
                "1970-10-12"
        );
    }

    public static ResponseEntity<GsakKlient.GsakRespons> lagGsakResponseEntity() {
        return lagGsakResponseEntity(8);
    }

    public static ResponseEntity<GsakKlient.GsakRespons> lagGsakResponseEntity(Integer gsakId) {
        return lagGsakResponseEntity(gsakId, HttpStatus.CREATED);
    }

    public static ResponseEntity<GsakKlient.GsakRespons> lagGsakResponseEntity(Integer gsakId, HttpStatus status) {
        GsakKlient.GsakRespons respons = new GsakKlient.GsakRespons(gsakId);
        return new ResponseEntity<>(respons, status);
    }
}
