package no.nav.tag.kontakt.oss.navenhetsmapping;

import no.nav.tag.kontakt.oss.norg.NorgKlient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NavEnhetUtils {
    private final Map<String, String> enheter;
    private final NorgKlient norgKlient;

    @Autowired
    public NavEnhetUtils(Map<String, String> enheter, NorgKlient norgKlient) {
        this.enheter = enheter;
        this.norgKlient = norgKlient;
        norgKlient.hentGeografiFraNorg();
    }

    public String mapFraKommunenrTilEnhetsnr(String kommunenr) {
        return enheter.get(kommunenr);
    }

}
