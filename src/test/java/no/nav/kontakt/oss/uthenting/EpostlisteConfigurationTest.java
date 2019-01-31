package no.nav.kontakt.oss.uthenting;

import no.nav.kontakt.oss.KontaktskjemaException;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.*;

public class EpostlisteConfigurationTest {

    @Test
    public void skalDekodeB64JsonEpostlisteTilMap() {
        EpostlisteConfiguration epostlisteConfiguration = new EpostlisteConfiguration();

        String json = "{\"Aremark\": [\"ostfold@nav.no\", \"geir@nav.no\"], \"Askim\": [\"ostfold@nav.no\"], \"Eidsberg\": [\"ostfold@nav.no\", \"anne@nav.no\", \"berit@nav.no\"]}";
        byte[] encoded = Base64.getEncoder().encode(json.getBytes());
        String epostlisteB64 = new String(encoded, StandardCharsets.UTF_8);

        Map<String, List<String>> onsketEpostliste = new HashMap<>();
        onsketEpostliste.put("Aremark", Arrays.asList("ostfold@nav.no", "geir@nav.no"));
        onsketEpostliste.put("Askim", Collections.singletonList("ostfold@nav.no"));
        onsketEpostliste.put("Eidsberg", Arrays.asList("ostfold@nav.no", "anne@nav.no", "berit@nav.no"));

        assertEquals(onsketEpostliste, epostlisteConfiguration.epostliste(epostlisteB64));
    }

    @Test(expected = KontaktskjemaException.class)
    public void skalFeileHvisEpostlistenErUgyldig() {
        String ugyldig = new String(Base64.getEncoder().encode("ugyldig".getBytes()), StandardCharsets.UTF_8);
        (new EpostlisteConfiguration()).epostliste(ugyldig);
    }

    @Test(expected = KontaktskjemaException.class)
    public void skalFeileHvisEpostlistenErNull() {
        (new EpostlisteConfiguration()).epostliste(null);
    }

}