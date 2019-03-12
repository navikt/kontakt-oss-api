package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon;

import no.nav.tag.kontakt.oss.KontaktskjemaException;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.Kommune;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static no.nav.tag.kontakt.oss.TestData.lesFil;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KodeverkKlientTest {

    @Mock
    private RestTemplate restTemplate;

    private KodeverkKlient kodeverkKlient;

    @Before
    public void setup() {
        kodeverkKlient = new KodeverkKlient(restTemplate, "kodeverkUrl");
    }

    @Test
    public void hentKommuner__skal_hente_kommuner_fra_json() {
        mockResponsFraKodeverk(lesFil("kommuner.json"));

        assertThat(kodeverkKlient.hentKommuner()).isEqualTo(Arrays.asList(
                new Kommune("1001", "Kristiansand"),
                new Kommune("1002", "Mandal")
        ));
    }

    @Test
    public void hentKommuner__skal_ikke_ta_med_kommuner_uten_beskrivelse() {
        mockResponsFraKodeverk("{ \"betydninger\": { \"1001\": [] } }");
        assertThat(kodeverkKlient.hentKommuner()).isEmpty();
    }

    @Test(expected = KontaktskjemaException.class)
    public void hentKommuner__skal_feile_hvis_respons_ikke_returnerer_gyldig_json() {
        mockResponsFraKodeverk("ikke gyldig json");
        kodeverkKlient.hentKommuner();
    }

    private void mockResponsFraKodeverk(String jsonResponse) {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                eq(String.class)
        )).thenReturn(
                new ResponseEntity<>(jsonResponse, HttpStatus.OK)
        );
    }
}