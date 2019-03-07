package no.nav.tag.kontakt.oss;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.NavEnhet;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Component
@Profile("dev")
public class MockServer {
    private WireMockServer server;
    private static ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @Autowired
    MockServer(
            @Value("${NORG_URL}") String norgUrl
    ) {
        boolean SKAL_MOCKE = true;
        if (!SKAL_MOCKE) {
            return;
        }

        log.info("STARTING MOCK SERVER");

        this.server =  new WireMockServer(7070);
        String norgPath = new URL(norgUrl).getPath();

        mockNorgGeografi(norgPath);
        mockNorgOrganisering(norgPath);
        mockNorgsMappingFraGeografiTilNavEnhet(norgPath);

        server.stubFor(get(urlEqualTo("/hello"))
                .willReturn(aResponse().withBody("hello world!"))
        );

        server.start();
    }

    @SneakyThrows
    private void mockNorgsMappingFraGeografiTilNavEnhet(String norgPath) {
        Map<String, NavEnhet> fraKommuneEllerBydelTilNavEnhet = objectMapper.readValue(
                hentStringFraFil("mapFraKommuneTilNavEnhet.json"),
                new TypeReference<Map<String, NavEnhet>>() {}
        );

        NavEnhet navEnhet;
        String navEnhetJson;

        for (String kommuneNrEllerBydelNr : fraKommuneEllerBydelTilNavEnhet.keySet()) {
            navEnhet = fraKommuneEllerBydelTilNavEnhet.get(kommuneNrEllerBydelNr);
            navEnhetJson = objectMapper.writeValueAsString(navEnhet);
            server.stubFor(
                    get(urlPathEqualTo(norgPath + "/enhet/navkontor/" + kommuneNrEllerBydelNr)).willReturn(aResponse()
                            .withStatus(HttpStatus.OK.value())
                            .withBody(navEnhetJson)
                    )
            );
        }
    }

    private void mockNorgOrganisering(String norgPath) {
        server.stubFor(
                get(urlPathEqualTo(norgPath + "/enhet/kontaktinformasjon/organisering/AKTIV")).willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withBody(hentStringFraFil("norgOrganisering.json"))
                )
        );

    }

    private void mockNorgGeografi(String norgPath) {
        server.stubFor(
                get(urlPathEqualTo(norgPath + "/kodeverk/geografi")).willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withBody(hentStringFraFil("norgGeografi.json"))
                )
        );
    }

    @SneakyThrows
    private String hentStringFraFil(String filnavn) {
        return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(filnavn), UTF_8);
    }
}
