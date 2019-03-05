package no.nav.tag.kontakt.oss;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URL;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Component
public class MockServer {
    private WireMockServer server;

    @SneakyThrows
    @Autowired
    MockServer(
            @Value("${NORG_URL}") String norgUrl
    ) {
        log.info("STARTING MOCK SERVER");

        this.server =  new WireMockServer(7070);

        String norgPath = new URL(norgUrl).getPath();

        String norgGeografiJson = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("norgGeografi.json"), UTF_8);

        server.stubFor(
                get(urlPathEqualTo(norgPath + "/kodeverk/geografi")).willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withBody(norgGeografiJson)
        ));

        server.stubFor(get(urlEqualTo("/hello"))
                .willReturn(aResponse().withBody("hello world!"))
        );

        server.start();
    }
}
