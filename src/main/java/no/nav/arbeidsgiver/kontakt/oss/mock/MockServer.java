package no.nav.arbeidsgiver.kontakt.oss.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URL;

@Slf4j
@Component
@Profile("local")
@ConditionalOnProperty(prefix = "mock", name = "enabled", havingValue = "true")
public class MockServer {
    private final WireMockServer server;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Autowired
    MockServer(
            ObjectMapper objectMapper,
            @Value("${norg.url}") String norgUrl,

            @Value("${mock.port}") Integer port
    ) {
        log.info("Starter mock-server");

        this.objectMapper = objectMapper;
        this.server = new WireMockServer(port);


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
    private String lesFilSomString(String filnavn) {
        return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("mock/" + filnavn));
    }
}
