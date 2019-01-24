package no.nav.tag.kontaktskjema;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class EnheterConfiguration {
    private String jsonString;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Bean("enheter")
    public Map<String, String> hentJsonString(@Value("${enheterFil:null}") String enheterFil) {
        File file;
        try {
            file = ResourceUtils.getFile(enheterFil);

            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            String json = new String(data, "UTF-8");

            Map<String, String> map = new HashMap<>();
            List<Enhet> liste;
            liste = objectMapper.readValue(json, new TypeReference<List<Enhet>>() {});
            liste.forEach(enhet -> map.put(enhet.getNummer(), enhet.getEnhet()));

            return map;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    @Data
    private static class Enhet {
        private String enhet;
        private String nummer;
        private String navn;
    }
}
