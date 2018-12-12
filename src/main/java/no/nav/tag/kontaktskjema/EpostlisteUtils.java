package no.nav.tag.kontaktskjema;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EpostlisteUtils {
    private static final Base64.Decoder decoder = Base64.getDecoder();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, List<String>> oversettTilMap(String epostlisteB64) throws IOException {
        byte[] decoded = decoder.decode(epostlisteB64);
        String json = new String(decoded, StandardCharsets.UTF_8);
        Map<String, List<String>> map = objectMapper.readValue(json, new TypeReference<Map<String, List<String>>>() {});
        return map == null ? new HashMap<>() : map;
    }

    public static String getMottakere(String epostlisteB64, Kontaktskjema kontaktskjema) throws IOException {
        List<String> mottakere = EpostlisteUtils.oversettTilMap(epostlisteB64).get(kontaktskjema.getKommune());
        return mottakere == null ? "" : mottakere.toString();
    }

}
