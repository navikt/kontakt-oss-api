package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon;

import no.nav.arbeidsgiver.kontakt.oss.KontaktskjemaException;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.Kommune;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.KommuneEllerBydel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static no.nav.arbeidsgiver.kontakt.oss.testUtils.TestData.lesFil;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KodeverkKlientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Captor
    ArgumentCaptor<HttpEntity<String>> requestCaptor;

    private KodeverkKlient kodeverkKlient;

    @BeforeEach
    public void setup() {
        when(restTemplateBuilder.errorHandler(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        kodeverkKlient = new KodeverkKlient(restTemplateBuilder, "kodeverkUrl");
    }

    @Test
    public void hentKommuner__skal_lese_produksjonsrespons_uten_feil() {
        mockKommuneRespons(lesFil("mock/kommuner.json"));
        assertThat(kodeverkKlient.hentKommuner().size()).isGreaterThan(400);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void hentKommuner__skal_sende_med_riktige_headers_til_kodeverk() {
        mockKommuneRespons(lesFil("kommuner.json"));

        kodeverkKlient.hentKommuner();

        captureKodeverkExchangeHeaders();
        HttpHeaders headers = requestCaptor.getValue().getHeaders();
        Assertions.assertThat(headers.get("Nav-Consumer-Id").get(0)).isEqualTo("kontakt-oss-api");
        Assertions.assertThat(headers.get("Nav-Call-Id").get(0)).isNotNull();
    }

    @Test
    public void hentKommuner__skal_hente_kommuner_fra_json() {
        mockKommuneRespons(lesFil("kommuner.json"));

        assertThat(kodeverkKlient.hentKommuner()).isEqualTo(Arrays.asList(
                new Kommune("1001", "Kristiansand"),
                new Kommune("1002", "Mandal")
        ));
    }

    @Test
    public void hentKommuner__skal_ikke_ta_med_kommuner_uten_beskrivelse() {
        mockKommuneRespons("{ \"betydninger\": { \"1001\": [] } }");
        assertThat(kodeverkKlient.hentKommuner()).isEmpty();
    }

    @Test
    public void hentKommuner__skal_feile_hvis_respons_ikke_returnerer_gyldig_json() {
        mockKommuneRespons("ikke gyldig json");
        assertThrows(KontaktskjemaException.class, () -> kodeverkKlient.hentKommuner());
    }

    @Test
    public void hentBydeler__skal_lese_produksjonsrespons_uten_feil() {
        mockBydelRespons(lesFil("mock/bydeler.json"));
        assertThat(kodeverkKlient.hentBydeler().size()).isGreaterThan(30);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void hentBydeler__skal_sende_med_riktige_headers_til_kodeverk() {
        mockBydelRespons(lesFil("bydeler.json"));

        kodeverkKlient.hentBydeler();

        captureKodeverkExchangeHeaders();
        HttpHeaders headers = requestCaptor.getValue().getHeaders();
        Assertions.assertThat(headers.get("Nav-Consumer-Id").get(0)).isEqualTo("kontakt-oss-api");
        Assertions.assertThat(headers.get("Nav-Call-Id").get(0)).isNotNull();
    }

    @Test
    public void hentBydeler__skal_hente_kommuner_fra_json() {
        mockBydelRespons(lesFil("bydeler.json"));

        assertThat(kodeverkKlient.hentBydeler()).isEqualTo(Arrays.asList(
                new KommuneEllerBydel("110301", "Hundvåg"),
                new KommuneEllerBydel("110302", "Tasta")
        ));
    }

    @Test
    public void hentBydeler__skal_ikke_ta_med_kommuner_uten_beskrivelse() {
        mockBydelRespons("{ \"betydninger\": { \"1001\": [] } }");
        assertThat(kodeverkKlient.hentBydeler()).isEmpty();
    }

    @Test
    public void hentBydeler__skal_feile_hvis_respons_ikke_returnerer_gyldig_json() {
        mockBydelRespons("ikke gyldig json");
        assertThrows(KontaktskjemaException.class, () -> kodeverkKlient.hentBydeler());
    }

    private void mockKommuneRespons(String jsonResponse) {
        mockResponsFraKodeverk(jsonResponse, ".*Kommuner.*");
    }

    private void mockBydelRespons(String jsonResponse) {
        mockResponsFraKodeverk(jsonResponse, ".*Bydeler.*");
    }

    private void mockResponsFraKodeverk(String jsonResponse, String pathMatch) {
        when(restTemplate.exchange(
                matches(pathMatch),
                eq(HttpMethod.GET),
                any(),
                eq(String.class)
        )).thenReturn(
                new ResponseEntity<>(jsonResponse, HttpStatus.OK)
        );
    }

    private void captureKodeverkExchangeHeaders() {
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), requestCaptor.capture(), eq(String.class));
    }
}
