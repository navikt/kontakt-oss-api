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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Component
@Profile("dev")
@ConditionalOnProperty(prefix = "mock", name = "enabled", havingValue = "true")
public class MockServer {
    private WireMockServer server;
    private static ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @Autowired
    MockServer(
            @Value("${norg.url}") String norgUrl,
            @Value("${mock.port}") Integer port
    ) {
        log.info("Starter mock-server");

        this.server =  new WireMockServer(port);
        String norgPath = new URL(norgUrl).getPath();

        mockNorgOrganisering(norgPath);
        mockNorgsMappingFraGeografiTilNavEnhet(norgPath);

        String kodeverkPath = "/kodeverk";
        mockKall(kodeverkPath + "/kodeverk/Kommuner/koder/betydninger", "kommuner.json");
        mockKall(kodeverkPath + "/kodeverk/Bydeler/koder/betydninger", "bydeler.json");

        server.start();
    }

    private void mockKall(String path, String filnavn) {
        server.stubFor(
                WireMock.get(WireMock.urlPathEqualTo(path)).willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withBody(hentStringFraFil(filnavn))
                )
        );
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

    @SneakyThrows
    private String hentStringFraFil(String filnavn) {
        return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("mock/" + filnavn), UTF_8);
    }
}
