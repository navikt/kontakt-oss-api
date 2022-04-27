package no.nav.arbeidsgiver.kontakt.oss.testUtils;

import lombok.SneakyThrows;
import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;
import no.nav.arbeidsgiver.kontakt.oss.TemaType;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndeling.*;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

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
                .navn("Ola Nordmann")
                .epost("ola.nordmann@fleskOgFisk.no")
                .telefonnr("01234567")
                .tema("Rekruttering")
                .temaType(TemaType.REKRUTTERING)
                .harSnakketMedAnsattrepresentant(false);
    }

    public static List<KommuneEllerBydel> hentTestKommuner(){
        List<KommuneEllerBydel> kommuner = new ArrayList<>();
        kommuner.add(new KommuneEllerBydel("Oslo", "1234"));
        kommuner.add(new KommuneEllerBydel("Bergen", "5678"));
        return kommuner;
    }

    @SneakyThrows
    public static String lesFil(String filnavn) {
        return IOUtils.toString(
                Objects.requireNonNull(TestData.class.getClassLoader().getResourceAsStream(filnavn)),
                StandardCharsets.UTF_8
        );
    }
}
