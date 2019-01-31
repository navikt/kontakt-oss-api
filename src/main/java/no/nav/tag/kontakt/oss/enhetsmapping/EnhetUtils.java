package no.nav.tag.kontakt.oss.enhetsmapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EnhetUtils {
    private final Map<String, String> enheter;

    @Autowired
    public EnhetUtils(Map<String, String> enheter) {
        this.enheter = enheter;
    }

    public String mapFraKommunenrTilEnhetsnr(String kommunenr) {
        return enheter.get(kommunenr);
    }

}
