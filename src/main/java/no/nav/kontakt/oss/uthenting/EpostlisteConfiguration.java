package no.nav.kontakt.oss.uthenting;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.kontakt.oss.KontaktskjemaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Configuration
public class EpostlisteConfiguration {

    private static final Base64.Decoder decoder = Base64.getDecoder();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Bean("epostliste")
    public Map<String, List<String>> epostliste(@Value("${epostliste:null}") String epostlisteB64) {
        return oversettTilMap(epostlisteB64);
    }

    private Map<String, List<String>> oversettTilMap(String epostlisteB64) {
        if (epostlisteB64 == null) {
            throw new KontaktskjemaException("Kunne ikke hente epostliste");
        }

        byte[] decoded = decoder.decode(epostlisteB64);
        String json = new String(decoded, StandardCharsets.UTF_8);
        Map<String, List<String>> map;

        try {
            map = objectMapper.readValue(json, new TypeReference<Map<String, List<String>>>() {});
        } catch (IOException e) {
            throw new KontaktskjemaException("Kunne ikke hente epostliste", e);
        }
        return map;
    }
}
