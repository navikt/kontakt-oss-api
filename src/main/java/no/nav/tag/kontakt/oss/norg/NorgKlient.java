package no.nav.tag.kontakt.oss.norg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class NorgKlient {
    private final RestTemplate restTemplate;
    private final String norgUrl;

    public NorgKlient(
            RestTemplate restTemplate,
            @Value("${NORG_URL:default}") String norgUrl
    ) {
        this.restTemplate = restTemplate;
        this.norgUrl = norgUrl;
    }

    public String hentGeografiFraNorg() {
        if (norgUrl.equals("default")) {
            return "default";
        }
        ResponseEntity<String> jsonResponse = restTemplate.getForEntity(
                norgUrl + "/kodeverk/geografi",
                String.class
        );
        System.out.println("You god NORG'd" + jsonResponse.getBody());
        return jsonResponse.getBody();
    }
}
