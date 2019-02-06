package no.nav.tag.kontakt.oss.gsak;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.DateProvider;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.gsak.integrasjon.GsakKlient;
import no.nav.tag.kontakt.oss.gsak.integrasjon.GsakRequest;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

// TODO TAG-233 Skal fjernes, er bare for å teste gsak-apiet direkte
@RestController
@Slf4j
public class GsakController {

    private final GsakKlient gsakKlient;

    @Autowired
    public GsakController(GsakKlient gsakKlient) {
        this.gsakKlient = gsakKlient;
    }

    @GetMapping(value = "${controller.basepath}/gsak")
    public ResponseEntity gsak() {
        MDC.put("correlationId", UUID.randomUUID().toString());
        MDC.put("first", "heihei!");
        log.info("Jeg logger");
        LocalDate aktivDato = new DateProvider().now().toLocalDate();
        Kontaktskjema kontaktskjema = new Kontaktskjema(
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

        String beskrivelse = String.format(
                "Arbeidsgiver har sendt henvendelse gjennom Kontaktskjema; \n" +
                        "Navn: %s \n" +
                        "Nummer: %s \n" +
                        "E-post: %s \n" +
                        "Kommune: %s (kommunenr: %s) \n" +
                        "Kontakt arbeidsgiver for å avklare hva henvendelsen gjelder. Husk å registrere henvendelsen som aktivitetstype «Kontaktskjema» i Arena.",
                kontaktskjema.getFornavn() + " " + kontaktskjema.getEtternavn(),
                kontaktskjema.getTelefonnr(),
                kontaktskjema.getEpost(),
                kontaktskjema.getKommune(),
                kontaktskjema.getKommunenr()
        );

        GsakRequest innsending = new GsakRequest(
                "0315",
                beskrivelse,
                "ARBD",
                "OPA",
                "VURD_HENV",
                "HOY",
                aktivDato.toString(),
                aktivDato.plusDays(2).toString()
        );
        ResponseEntity<Integer> response = ResponseEntity.ok(gsakKlient.opprettGsakOppgave(innsending));
        MDC.clear();
        return response;
    }
}
