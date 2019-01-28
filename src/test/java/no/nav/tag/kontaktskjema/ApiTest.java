package no.nav.tag.kontaktskjema;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.DEFINED_PORT)
public class ApiTest {

    @Test
    public void postKontaktskjema_OK() throws Exception {

        String body = "{ " +
            "\"bedriftsnavn\": \"testbedrift\"," +
            "\"epost\": \"test@testesen.no\"," +
            "\"etternavn\": \"testesen\"," +
            "\"fornavn\": \"test\"," +
            "\"fylke\": \"agder\"," +
            "\"kommune\": \"Audnedal\"," +
            "\"kommunenr\": \"1027\"," +
            "\"telefonnr\": \"1234\"" +
            "}";

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/kontaktskjema/meldInteresse"))
                .header("Accept", "*/*")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "nb-NO,nb;q=0.9,no;q=0.8,nn;q=0.7,en-US;q=0.6,en;q=0.5")
                .header("Cache-Control", "no-cache")
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(body))
                .build();

        HttpResponse<?> response = client.send(request, BodyHandlers.ofString());
        assertThat(response.statusCode(), is(200));
        assertThat(response.body(), is("\"OK\""));
    }

    @Test
    public void postKontaktskjema_feil_kommunenr() throws Exception {

        String body = "{ " +
            "\"bedriftsnavn\": \"testbedrift\"," +
            "\"epost\": \"test@testesen.no\"," +
            "\"etternavn\": \"testesen\"," +
            "\"fornavn\": \"test\"," +
            "\"fylke\": \"agder\"," +
            "\"kommune\": \"Audnedal\"," +
            "\"kommunenr\": \"kommunenr\"," +
            "\"telefonnr\": \"1234\"" +
            "}";

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/kontaktskjema/meldInteresse"))
                .header("Accept", "*/*")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "nb-NO,nb;q=0.9,no;q=0.8,nn;q=0.7,en-US;q=0.6,en;q=0.5")
                .header("Cache-Control", "no-cache")
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(body))
                .build();

        HttpResponse<?> response = client.send(request, BodyHandlers.ofString());
        assertThat(response.statusCode(), is(500));
        assertThat(response.body(), is(""));
    }
    
}
