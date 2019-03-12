package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon;

import no.nav.tag.kontakt.oss.KontaktskjemaException;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.NavEnhet;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgKlient;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgOrganisering;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static no.nav.tag.kontakt.oss.TestData.lesFil;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NorgKlientTest {

    @Mock
    private RestTemplate restTemplate;

    @Captor
    ArgumentCaptor<HttpEntity<String>> requestCaptor;

    private NorgKlient norgKlient;

    @Before
    public void setUp() {
        norgKlient = new NorgKlient(restTemplate, "");
    }

    @Test
    public void hentOrganiseringFraNorg__skal_oversette_til_riktig_objekt() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>(lesFil("norgOrganiseringReellRespons.json"), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(responseEntity);

        List<NorgOrganisering> organisering = norgKlient.hentOrganiseringFraNorg();
        assertThat(organisering).isEqualTo(Arrays.asList(
                new NorgOrganisering("9999","Inaktiv","1500"),
                new NorgOrganisering("1416","Aktiv","1400")
        ));
    }


    @Test(expected = KontaktskjemaException.class)
    public void hentOrganiseringFraNorg__skal_feile_hvis_respons_ikke_returnerer_ok() {
        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(responseEntity);
        norgKlient.hentOrganiseringFraNorg();
    }

    @Test(expected = KontaktskjemaException.class)
    public void hentOrganiseringFraNorg__skal_feile_hvis_respons_ikke_returnerer_gyldig_json() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("{ikke gyldig json}", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(responseEntity);
        norgKlient.hentOrganiseringFraNorg();
    }

    @Test
    public void hentOrganiseringFraNorg__skal_sette_consumerId() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("[]", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(responseEntity);

        norgKlient.hentOrganiseringFraNorg();

        captureNorgExchangeHeaders();
        HttpHeaders headers = requestCaptor.getValue().getHeaders();
        Assertions.assertThat(headers.get("consumerId").get(0)).isEqualTo("kontakt-oss-api");
    }

    @Test
    public void skal__handtere_at_overordnetEnhet_er_null() {
        String jsonResponse = "[{" +
                "\"enhet\": { \"enhetNr\": \"4280\", \"status\": \"Aktiv\" }," +
                "\"overordnetEnhet\": null" +
        "}]";

        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).thenReturn(responseEntity);

        assertThat(norgKlient.hentOrganiseringFraNorg()).isEqualTo(Collections.singletonList(
                new NorgOrganisering("4280", "Aktiv", null))
        );
    }

    @Test
    public void hentTilhoerendeNavenhet__skal_returnere_enhetsnr_hvis_OK() {
        String jsonResponse = "{\"enhetNr\": \"4444\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);
        assertThat(norgKlient.hentTilhoerendeNavEnhet("1111").get()).isEqualTo(new NavEnhet("4444"));
    }

    @Test
    public void hentTilhoerendeNavenhet__skal_returnere_empty_hvis_NOT_FOUND() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);
        assertThat(norgKlient.hentTilhoerendeNavEnhet("1111")).isEmpty();
    }

    private void captureNorgExchangeHeaders() {
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), requestCaptor.capture(), eq(String.class));
    }
}