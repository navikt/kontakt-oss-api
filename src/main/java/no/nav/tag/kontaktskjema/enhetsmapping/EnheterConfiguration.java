package no.nav.tag.kontaktskjema.enhetsmapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
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
        String enheterFilnavn = "classpath:enheter.json";
        File file = ResourceUtils.getFile(enheterFilnavn);
        return FileUtils.readFileToString(file, "UTF-8");
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
