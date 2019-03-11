package no.nav.tag.kontakt.oss;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

import static java.net.http.HttpClient.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"mock.enabled=false"})
public class ApiTest {

    @LocalServerPort
    private String port;

    private static final String OK_KONTAKTSKJEMA_JSON = "{ " +
        "\"bedriftsnavn\": \"testbedrift\"," +
        "\"epost\": \"test@testesen.no\"," +
        "\"etternavn\": \"testesen\"," +
        "\"fornavn\": \"test\"," +
        "\"fylke\": \"agder\"," +
        "\"kommune\": \"Audnedal\"," +
        "\"kommunenr\": \"1027\"," +
        "\"telefonnr\": \"1234\"" +
        "}";

    @Test
    public void postKontaktskjema_OK() throws Exception {

        HttpResponse<?> response = newBuilder().build().send(createRequest(OK_KONTAKTSKJEMA_JSON), ofString());
        assertThat(response.statusCode(), is(200));
        assertThat(response.body(), is("\"OK\""));
    }

    private HttpRequest createRequest(String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/meldInteresse"))
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

        String bodyMedForLangtKommunenr = OK_KONTAKTSKJEMA_JSON.replaceFirst("1027", "10127");

        HttpResponse<?> response = HttpClient.newBuilder().build().send(createRequest(bodyMedForLangtKommunenr), ofString());
        assertThat(response.statusCode(), is(500));
        assertThat(response.body(), is(""));
    }

}
