package no.nav.tag.kontakt.oss.gsak.integrasjon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.KontaktskjemaException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.MDC;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.UUID;

import static no.nav.tag.kontakt.oss.TestData.lagGsakRequest;
import static no.nav.tag.kontakt.oss.TestData.lagGsakResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GsakKlientTest {

    @Mock
    RestTemplate restTemplate;

    @Captor
    ArgumentCaptor<HttpEntity<String>> requestCaptor;

    private GsakKlient gsakKlient;

    @Before
    public void setUp() {
        mockReturverdiFraGsak(lagGsakResponseEntity());
        MDC.put("correlationId", "dummy");
        gsakKlient = new GsakKlient(restTemplate, "");
    }

    @Test
    public void opprettGsakOppgave__skal_returnere_id_til_opprettet_gsakoppgave() {
        Integer gsakId = 99;
        mockReturverdiFraGsak(lagGsakResponseEntity(gsakId));

        Integer opprettetGsakId = gsakKlient.opprettGsakOppgave(lagGsakRequest());

        assertThat(opprettetGsakId).isEqualTo(gsakId);
    }

    @Test
    public void opprettGsakOppgave__skal_sende_med_riktig_content_type() {
        gsakKlient.opprettGsakOppgave(lagGsakRequest());

        captureGsakRequest();
        assertThat(requestCaptor.getValue().getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    public void opprettGsakOppgave__skal_sende_med_correlation_id() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);

        gsakKlient.opprettGsakOppgave(lagGsakRequest());

        captureGsakRequest();
        HttpHeaders headers = requestCaptor.getValue().getHeaders();
        assertThat(headers.get("X-Correlation-ID").get(0)).isEqualTo(correlationId);
    }

    @Test(expected = KontaktskjemaException.class)
    public void opprettGsakOppgave__skal_feile_hvis_correlation_id_ikke_er_satt() {
        MDC.clear();
        gsakKlient.opprettGsakOppgave(lagGsakRequest());
    }

    @Test(expected = KontaktskjemaException.class)
    public void opprettGsakOppgave__skal_feile_hvis_respons_ikke_returnerer_created() {
        mockReturverdiFraGsak(lagGsakResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR));
        gsakKlient.opprettGsakOppgave(lagGsakRequest());
    }

    @Test(expected = KontaktskjemaException.class)
    public void opprettGsakOppgave__skal_feile_hvis_respons_ikke_returnerer_gyldig_json() {
        ResponseEntity<String> respons = new ResponseEntity<>("{ikke gyldig json}", HttpStatus.CREATED);
        mockReturverdiFraGsak(respons);
        gsakKlient.opprettGsakOppgave(lagGsakRequest());
    }

    private void captureGsakRequest() {
        verify(restTemplate, times(1)).postForEntity(anyString(), requestCaptor.capture(), eq(String.class));
    }

    private void mockReturverdiFraGsak(ResponseEntity<String> gsakEntity) {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(gsakEntity);
    }
}