package no.nav.tag.kontakt.oss;

import no.nav.tag.kontakt.oss.gsak.integrasjon.GsakRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
                "9999",
                "123456789",
                "beskrivelse",
                "ARBD",
                "OPA",
                "VURD_HENV",
                "HOY",
                "1970-10-10",
                "1970-10-12"
        );
    }

    public static ResponseEntity<String> lagGsakResponseEntity() {
        return lagGsakResponseEntity(8);
    }

    public static ResponseEntity<String> lagGsakResponseEntity(HttpStatus status) {
        return lagGsakResponseEntity(8, status);
    }

    public static ResponseEntity<String> lagGsakResponseEntity(Integer gsakId) {
        return lagGsakResponseEntity(gsakId, HttpStatus.CREATED);
    }

    public static ResponseEntity<String> lagGsakResponseEntity(Integer gsakId, HttpStatus status) {
        String responsBody = String.format("{\"id\": %d}", gsakId);
        return new ResponseEntity<>(responsBody, status);
    }
}
