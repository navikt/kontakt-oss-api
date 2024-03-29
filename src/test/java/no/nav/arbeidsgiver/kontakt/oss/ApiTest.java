package no.nav.arbeidsgiver.kontakt.oss;

import lombok.SneakyThrows;
import no.nav.arbeidsgiver.kontakt.oss.testUtils.DatabasePopulator;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

import static java.net.http.HttpClient.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"local"})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ApiTest {

    @LocalServerPort
    private String port;

    private static final String OK_KONTAKTSKJEMA_JSON = lesFil("kontaktskjema_ok.json");

    @Autowired
    private DatabasePopulator databasePopulator;

    @SneakyThrows
    private static String lesFil(String name) {
        return IOUtils.toString(
                ApiTest.class.getClassLoader().getResourceAsStream(name)
        );
    }

    @BeforeEach
    @SneakyThrows
    public void populerDatabase() {
        databasePopulator.populerFylkesinndelingRepositoryForÅUnngåNullpointers();
    }

    @Test
    public void postKontaktskjema_OK() throws Exception {
        HttpResponse<?> response = newBuilder().build().send(createRequest(OK_KONTAKTSKJEMA_JSON), ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("\"OK\"");
    }

    private HttpRequest createRequest(String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/kontakt-oss-api/meldInteresse"))
                .header("Accept", "*/*")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "nb-NO,nb;q=0.9,no;q=0.8,nn;q=0.7,en-US;q=0.6,en;q=0.5")
                .header("Cache-Control", "no-cache")
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(body))
                .build();
    }

    @Test
    public void postKontaktskjema_feil_kommunenr() throws Exception {

        String bodyMedForLangtKommunenr = OK_KONTAKTSKJEMA_JSON.replaceFirst("1841", "1084109");

        HttpResponse<?> response = HttpClient.newBuilder().build().send(createRequest(bodyMedForLangtKommunenr), ofString());
        assertThat(response.statusCode()).isEqualTo(400);
    }

}
