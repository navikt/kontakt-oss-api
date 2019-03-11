package no.nav.tag.kontakt.oss.mock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
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
            @Value("${norg.url}") String norgUrl,
            @Value("${mock.port}") Integer port
    ) {
        if (true) { return; }
        log.info("Starter mock-server");

        this.server =  new WireMockServer(port);
        String norgPath = new URL(norgUrl).getPath();

        mockNorgGeografi(norgPath);
        mockNorgOrganisering(norgPath);
        mockNorgsMappingFraGeografiTilNavEnhet(norgPath);

        try {
            server.start();
        } catch (Exception e) {
            if (!server.isRunning()) {
                log.info("Fikk ikke startet mock-server", e);
            }
        }
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
                    WireMock.get(WireMock.urlPathEqualTo(norgPath + "/enhet/navkontor/" + kommuneNrEllerBydelNr)).willReturn(WireMock.aResponse()
                            .withStatus(HttpStatus.OK.value())
                            .withBody(navEnhetJson)
                    )
            );
        }
    }

    private void mockNorgOrganisering(String norgPath) {
        server.stubFor(
                WireMock.get(WireMock.urlPathEqualTo(norgPath + "/enhet/kontaktinformasjon/organisering/AKTIV")).willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withBody(hentStringFraFil("norgOrganisering.json"))
                )
        );

    }

    private void mockNorgGeografi(String norgPath) {
        server.stubFor(
                WireMock.get(WireMock.urlPathEqualTo(norgPath + "/kodeverk/geografi")).willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withBody(hentStringFraFil("norgGeografi.json"))
                )
        );
    }

    @SneakyThrows
    private String hentStringFraFil(String filnavn) {
        return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("mock/" + filnavn), UTF_8);
    }
}