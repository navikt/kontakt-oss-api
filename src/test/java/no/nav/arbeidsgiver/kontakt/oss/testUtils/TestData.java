package no.nav.arbeidsgiver.kontakt.oss.testUtils;

import lombok.SneakyThrows;
import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;
import no.nav.arbeidsgiver.kontakt.oss.TemaType;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.*;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TestData {
    public static final String FYLKESENHETNR_TIL_MOERE_OG_ROMSDAL = "1500";

    public static Kontaktskjema kontaktskjema() {
        return kontaktskjemaBuilder().build();
    }

    public static Kontaktskjema.KontaktskjemaBuilder kontaktskjemaBuilder() {
        return Kontaktskjema.builder()
                .id(null)
                .opprettet(LocalDateTime.now())
                .fylkesenhetsnr(FYLKESENHETNR_TIL_MOERE_OG_ROMSDAL)
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

    public static Map<String, List<KommuneEllerBydel>> fraFylkesenheterTilKommuner() {
        return new HashMap<>() {{
            put("fylke1", Arrays.asList(kommune("1.1"), kommune("1.2"), kommune("1.3")));
            put("fylke2", Arrays.asList(kommune("2.1"), kommune("2.2"), kommune("2.3")));
            put("fylke3", Arrays.asList(kommune("3.1"), bydel("3.2"), bydel("3.3")));
        }};
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
        return IOUtils.toString(
                Objects.requireNonNull(TestData.class.getClassLoader().getResourceAsStream(filnavn)),
                StandardCharsets.UTF_8
        );
    }
}
