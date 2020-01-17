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
@Profile("local")
@ConditionalOnProperty(prefix = "mock", name = "enabled", havingValue = "true")
public class MockServer {
    private WireMockServer server;
    private static ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @Autowired
    MockServer(
            @Value("${norg.url}") String norgUrl,
            @Value("${gsak.url}") String gsakUrl,
            @Value("${kodeverk.url}") String kodeverkUrl,
            @Value("${salesforce.auth.url}") String salesforceAuthUrl,
            @Value("${salesforce.contactform.url}") String salesforceApiUrl,
            @Value("${mock.port}") Integer port
    ) {
        log.info("Starter mock-server");

        this.server = new WireMockServer(port);
        String norgPath = new URL(norgUrl).getPath();
        String kodeverkPath = new URL(kodeverkUrl).getPath();
        String gsakPath = new URL(gsakUrl).getPath();
        String salesforceAuthPath = new URL(salesforceAuthUrl).getPath();
        String salesforceApiPath = new URL(salesforceApiUrl).getPath();

        mockNorgsMappingFraGeografiTilNavEnhet(norgPath);
        mockResponsFraGsak(gsakPath);
        mockKallFraFil(norgPath + "/enhet/kontaktinformasjon/organisering/AKTIV", "norgOrganisering.json");
        mockKallFraFil(kodeverkPath + "/kodeverk/Kommuner/koder/betydninger", "kommuner.json");
        mockKallFraFil(kodeverkPath + "/kodeverk/Bydeler/koder/betydninger", "bydeler.json");
        mockPost(salesforceAuthPath, "{ \"access_token\": \"heisann\" }");
        mockPost(salesforceApiPath, "{}");

        server.start();
    }

    private void mockKallFraFil(String path, String filnavn) {
        mockGet(path, lesFilSomString(filnavn));
    }

    private void mockGet(String path, String body) {
        server.stubFor(
                WireMock.get(WireMock.urlPathEqualTo(path)).willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HttpStatus.OK.value())
                        .withBody(body)
                )
        );
    }

    private void mockPost(String path, String body) {
        server.stubFor(
                WireMock.post(WireMock.urlPathEqualTo(path)).willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HttpStatus.OK.value())
                        .withBody(body)
                )
        );
    }

    @SneakyThrows
    private void mockNorgsMappingFraGeografiTilNavEnhet(String norgPath) {
        Map<String, NavEnhet> fraKommuneEllerBydelTilNavEnhet = objectMapper.readValue(
                lesFilSomString("mapFraKommuneTilNavEnhet.json"),
                new TypeReference<Map<String, NavEnhet>>() {
                }
        );

        NavEnhet navEnhet;
        String navEnhetJson;

        for (String kommuneNrEllerBydelNr : fraKommuneEllerBydelTilNavEnhet.keySet()) {
            navEnhet = fraKommuneEllerBydelTilNavEnhet.get(kommuneNrEllerBydelNr);
            navEnhetJson = objectMapper.writeValueAsString(navEnhet);
            mockGet(norgPath + "/enhet/navkontor/" + kommuneNrEllerBydelNr, navEnhetJson);
        }
    }

    private void mockResponsFraGsak(String gsakPath) {
        server.stubFor(
                WireMock.post(WireMock.urlPathEqualTo(gsakPath)).willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HttpStatus.CREATED.value())
                        .withBody("{\"id\": 123456}")
                )
        );
    }

    @SneakyThrows
    private String lesFilSomString(String filnavn) {
        return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("mock/" + filnavn), UTF_8);
    }
}
