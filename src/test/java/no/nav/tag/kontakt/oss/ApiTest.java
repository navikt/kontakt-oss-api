package no.nav.tag.kontakt.oss;

import lombok.SneakyThrows;
import no.nav.tag.kontakt.oss.testUtils.DatabasePopulator;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.DependsOn;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

import static java.net.http.HttpClient.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"mock.enabled=false"})
@DependsOn("databasePopulator")
public class ApiTest {

    @LocalServerPort
    private String port;

    private static final String OK_KONTAKTSKJEMA_JSON = lesFil("kontaktskjema_ok.json");

    @Autowired
    private DatabasePopulator databasePopulator;

    @SneakyThrows
    private static String lesFil(String name) {
        return IOUtils.toString(
                ApiTest.class.getClassLoader().getResourceAsStream(name),
                UTF_8
        );
    }

    @Before
    @SneakyThrows
    public void populerDatabase() {
        databasePopulator.populerFylkesinndelingRepositoryHvisTomForÅUnngåNullpointers();
    }

    @Test
    public void postKontaktskjema_OK() throws Exception {
        HttpResponse<?> response = newBuilder().build().send(createRequest(OK_KONTAKTSKJEMA_JSON), ofString());
        assertThat(response.statusCode(), is(200));
        assertThat(response.body(), is("\"OK\""));
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
        assertThat(response.statusCode(), is(400));
    }

}
