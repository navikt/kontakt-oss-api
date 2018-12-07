package no.nav.tag.kontaktskjema;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.*;

public class EpostlisteUtilsTest {

    @Test
    public void skalDekodeB64JsonEpostlisteTilMap() throws IOException {
        String json = "{\"Aremark\": [\"ostfold@nav.no\", \"geir@nav.no\"], \"Askim\": [\"ostfold@nav.no\"], \"Eidsberg\": [\"ostfold@nav.no\", \"anne@nav.no\", \"berit@nav.no\"]}";
        byte[] encoded = Base64.getEncoder().encode(json.getBytes());
        String epostlisteB64 = new String(encoded, StandardCharsets.UTF_8);

        Map<String, List<String>> onsketEpostliste = new HashMap<>();
        onsketEpostliste.put("Aremark", Arrays.asList("ostfold@nav.no", "geir@nav.no"));
        onsketEpostliste.put("Askim", Collections.singletonList("ostfold@nav.no"));
        onsketEpostliste.put("Eidsberg", Arrays.asList("ostfold@nav.no", "anne@nav.no", "berit@nav.no"));

        assertEquals(onsketEpostliste, EpostlisteUtils.oversettTilMap(epostlisteB64));
    }

}