package no.nav.arbeidsgiver.kontakt.oss.testUtils;

import lombok.SneakyThrows;
import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;
import no.nav.arbeidsgiver.kontakt.oss.TemaType;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.Bydel;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.FylkesinndelingMedNavEnheter;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.Kommune;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.KommuneEllerBydel;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.NavEnhet;
import no.nav.arbeidsgiver.kontakt.oss.gsak.integrasjon.GsakRequest;
import no.nav.arbeidsgiver.kontakt.oss.salesforce.ContactForm;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static no.nav.arbeidsgiver.kontakt.oss.gsak.GsakOppgaveService.FYLKESENHETNR_TIL_MØRE_OG_ROMSDAL;

public class TestData {
    public static Kontaktskjema kontaktskjema() {
        return kontaktskjemaBuilder().build();
    }

    public static Kontaktskjema.KontaktskjemaBuilder kontaktskjemaBuilder() {
        return Kontaktskjema.builder()
                .id(null)
                .opprettet(LocalDateTime.now())
                .fylkesenhetsnr(FYLKESENHETNR_TIL_MØRE_OG_ROMSDAL)
                .kommune("Bodø")
                .kommunenr("1804")
                .bedriftsnavn("Flesk og Fisk AS")
                .orgnr("979312059")
                .fornavn("Ola")
                .etternavn("Nordmann")
                .epost("ola.nordmann@fleskOgFisk.no")
                .telefonnr("01234567")
                .tema("Rekruttering")
                .temaType(TemaType.REKRUTTERING)
                .harSnakketMedAnsattrepresentant(false);
    }

    public static ContactForm contactForm() {
        Kontaktskjema kontaktskjema = kontaktskjema();
        return new ContactForm(
                kontaktskjema.getTemaType(),
                kontaktskjema.getFylkesenhetsnr(),
                kontaktskjema.getKommunenr(),
                kontaktskjema.getBedriftsnavn(),
                kontaktskjema.getOrgnr(),
                kontaktskjema.getFornavn(),
                kontaktskjema.getEtternavn(),
                kontaktskjema.getEpost(),
                kontaktskjema.getTelefonnr()
        );
    }

    public static GsakRequest gsakRequest() {
        return new GsakRequest(
                "0000",
                "9999",
                "979312059",
                "beskrivelse",
                "ARBD",
                "OPA",
                "VURD_HENV",
                "HOY",
                "1970-10-10",
                "1970-10-12"
        );
    }

    public static ResponseEntity<String> gsakResponseEntity() {
        return gsakResponseEntity(8);
    }

    public static ResponseEntity<String> gsakResponseEntity(HttpStatus status) {
        return gsakResponseEntity(8, status);
    }

    public static ResponseEntity<String> gsakResponseEntity(Integer gsakId) {
        return gsakResponseEntity(gsakId, HttpStatus.CREATED);
    }

    public static ResponseEntity<String> gsakResponseEntity(Integer gsakId, HttpStatus status) {
        String responsBody = String.format("{\"id\": %d}", gsakId);
        return new ResponseEntity<>(responsBody, status);
    }

    public static Map<String, NavEnhet> fraKommuneNrTilNavEnhet() {
        Map<KommuneEllerBydel, NavEnhet> map = fraKommuneTilNavEnhet();
        return map.keySet().stream().collect(Collectors.toMap(
                KommuneEllerBydel::getNummer,
                map::get
        ));
    }

    public static Map<KommuneEllerBydel, NavEnhet> fraKommuneTilNavEnhet() {
        return new HashMap<>() {{
            put(kommune("1.1"), navEnhet("1.1"));
            put(kommune("1.2"), navEnhet("1.2"));
            put(kommune("1.3"), navEnhet("1.3"));
            put(kommune("2.1"), navEnhet("2.1"));
            put(kommune("2.2"), navEnhet("2.2"));
            put(kommune("2.3"), navEnhet("2.3"));
            put(kommune("3.1"), navEnhet("3.1"));
            put(kommune("3.2"), navEnhet("3.2"));
            put(kommune("3.3"), navEnhet("3.3"));
        }};
    }

    public static FylkesinndelingMedNavEnheter fraFylkesenheterTilKommuner() {
        return new FylkesinndelingMedNavEnheter(new HashMap<>() {{
            put("fylke1", Arrays.asList(kommune("1.1"), kommune("1.2"), kommune("1.3")));
            put("fylke2", Arrays.asList(kommune("2.1"), kommune("2.2"), kommune("2.3")));
            put("fylke3", Arrays.asList(kommune("3.1"), bydel("3.2"), bydel("3.3")));
        }});
    }

    public static NavEnhet navEnhet(String id) {
        return new NavEnhet(id);
    }

    public static Kommune kommune(String id) {
        return new Kommune(id, id);
    }

    public static Bydel bydel(String id) {
        return new Bydel(id, id);
    }

    @SneakyThrows
    public static String lesFil(String filnavn) {
        return IOUtils.toString(TestData.class.getClassLoader().getResourceAsStream(filnavn));
    }
}
