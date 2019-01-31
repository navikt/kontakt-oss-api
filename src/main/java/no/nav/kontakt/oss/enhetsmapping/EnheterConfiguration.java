package no.nav.kontakt.oss.enhetsmapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class EnheterConfiguration {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Bean("enheter")
    public Map<String, String> hentJsonString() {
        try {
            String json = lesEnheterJson();
            return oversettTilMapOverEnheter(json);
        } catch (IOException e) {
            throw new RuntimeException("Innlesing av konfigurasjonsfil for enhetsinformasjon feilet.", e);
        }
    }

    private String lesEnheterJson() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("enheter.json");
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }

    private Map<String, String> oversettTilMapOverEnheter(String enheterJson) throws IOException {
        Map<String, String> map = new HashMap<>();
        List<KommuneInfo> liste;
        liste = objectMapper.readValue(enheterJson, new TypeReference<List<KommuneInfo>>() {});
        liste.forEach(enhet -> map.put(enhet.getNummer(), enhet.getEnhet()));
        return map;
    }

    @Data
    private static class KommuneInfo {
        private String enhet;
        private String nummer;
        private String navn;
    }
}
