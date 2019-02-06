package no.nav.tag.kontakt.oss.gsak.integrasjon;

import no.nav.tag.kontakt.oss.KontaktskjemaException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.MDC;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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
    ArgumentCaptor<HttpEntity<GsakRequest>> requestCaptor;

    @Before
    public void setUp() {
        mockReturverdiFraGsak(lagGsakResponseEntity());
        MDC.put("correlationId", "dummy");
    }

    @Test
    public void opprettGsakOppgave__skal_returnere_id_til_opprettet_gsakoppgave() {
        Integer gsakId = 99;
        mockReturverdiFraGsak(lagGsakResponseEntity(gsakId));
        GsakKlient gsakKlient = new GsakKlient(restTemplate, "");

        Integer opprettetGsakId = gsakKlient.opprettGsakOppgave(lagGsakRequest());

        assertThat(opprettetGsakId).isEqualTo(gsakId);
    }

    @Test
    public void opprettGsakOppgave__skal_sende_med_riktig_content_type() {
        GsakKlient gsakKlient = new GsakKlient(restTemplate, "");

        gsakKlient.opprettGsakOppgave(lagGsakRequest());

        captureGsakRequest();
        assertThat(requestCaptor.getValue().getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    public void opprettGsakOppgave__skal_sende_med_correlation_id() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        GsakKlient gsakKlient = new GsakKlient(restTemplate, "");

        gsakKlient.opprettGsakOppgave(lagGsakRequest());

        captureGsakRequest();
        HttpHeaders headers = requestCaptor.getValue().getHeaders();
        assertThat(headers.get("X-Correlation-ID").get(0)).isEqualTo(correlationId);
    }

    @Test(expected = KontaktskjemaException.class)
    public void opprettGsakOppgave__skal_feile_hvis_correlation_id_ikke_er_satt() {
        MDC.clear();
        new GsakKlient(restTemplate, "").opprettGsakOppgave(lagGsakRequest());
    }

    private void captureGsakRequest() {
        verify(restTemplate, times(1)).postForEntity(anyString(), requestCaptor.capture(), eq(GsakKlient.GsakRespons.class));
    }

    private void mockReturverdiFraGsak(ResponseEntity<GsakKlient.GsakRespons> gsakEntity) {
        when(restTemplate.postForEntity(anyString(), any(), eq(GsakKlient.GsakRespons.class))).thenReturn(gsakEntity);
    }
}