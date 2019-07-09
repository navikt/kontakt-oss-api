package no.nav.tag.kontakt.oss.gsak.integrasjon;

import no.nav.tag.kontakt.oss.BadRequestException;
import no.nav.tag.kontakt.oss.KontaktskjemaException;
import no.nav.tag.kontakt.oss.TestData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.MDC;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static no.nav.tag.kontakt.oss.TestData.gsakRequest;
import static no.nav.tag.kontakt.oss.TestData.gsakResponseEntity;
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
        mockReturverdiFraGsak(TestData.gsakResponseEntity());
        MDC.put("correlationId", "dummy");
        gsakKlient = new GsakKlient(restTemplate, "");
    }

    @Test
    public void opprettGsakOppgave__skal_returnere_id_til_opprettet_gsakoppgave() {
        Integer gsakId = 99;
        mockReturverdiFraGsak(TestData.gsakResponseEntity(gsakId));

        Integer opprettetGsakId = gsakKlient.opprettGsakOppgave(gsakRequest());

        assertThat(opprettetGsakId).isEqualTo(gsakId);
    }

    @Test
    public void opprettGsakOppgave__skal_sende_med_riktig_content_type() {
        gsakKlient.opprettGsakOppgave(gsakRequest());

        captureGsakRequest();
        assertThat(requestCaptor.getValue().getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    public void opprettGsakOppgave__skal_sende_med_correlation_id() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);

        gsakKlient.opprettGsakOppgave(gsakRequest());

        captureGsakRequest();
        HttpHeaders headers = requestCaptor.getValue().getHeaders();
        assertThat(headers.get("X-Correlation-ID").get(0)).isEqualTo(correlationId);
    }

    @Test(expected = KontaktskjemaException.class)
    public void opprettGsakOppgave__skal_feile_hvis_correlation_id_ikke_er_satt() {
        MDC.clear();
        gsakKlient.opprettGsakOppgave(gsakRequest());
    }

    @Test(expected = KontaktskjemaException.class)
    public void opprettGsakOppgave__skal_feile_hvis_respons_ikke_returnerer_created() {
        mockReturverdiFraGsak(TestData.gsakResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR));
        gsakKlient.opprettGsakOppgave(gsakRequest());
    }

    @Test(expected = KontaktskjemaException.class)
    public void opprettGsakOppgave__skal_feile_hvis_respons_ikke_returnerer_gyldig_json() {
        ResponseEntity<String> respons = new ResponseEntity<>("{ikke gyldig json}", HttpStatus.CREATED);
        mockReturverdiFraGsak(respons);
        gsakKlient.opprettGsakOppgave(gsakRequest());
    }

    @Test
    public void opprettGsakOppgave__skal_parse_json() {
        String json = "{\"id\":8888669,\"tildeltEnhetsnr\":\"0315\",\"opprettetAvEnhetsnr\":\"9999\",\"orgnr\":\"123456789\",\"beskrivelse\":\"Arbeidsgiver har sendt henvendelse gjennom Kontaktskjema; \\nNavn: Ola Nordmann \\nNummer: 01234567 \\nE-post: ola.nordmann@fleskOgFisk.no \\nKommune: BodÃ¸ (kommunenr: 0011) \\nKontakt arbeidsgiver for Ã¥ avklare hva henvendelsen gjelder. Husk Ã¥ registrere henvendelsen som aktivitetstype Â«KontaktskjemaÂ» i Arena.\",\"temagruppe\":\"ARBD\",\"tema\":\"OPA\",\"oppgavetype\":\"VURD_HENV\",\"versjon\":1,\"fristFerdigstillelse\":\"2019-02-09\",\"aktivDato\":\"2019-02-07\",\"opprettetTidspunkt\":\"2019-02-07T11:56:57.675+01:00\",\"opprettetAv\":\"srvtag-kontaktskjema\",\"prioritet\":\"HOY\",\"status\":\"OPPRETTET\",\"metadata\":{}}";
        ResponseEntity<String> respons = new ResponseEntity<>(json, HttpStatus.CREATED);
        mockReturverdiFraGsak(respons);
        gsakKlient.opprettGsakOppgave(gsakRequest());
    }


    @Test(expected = BadRequestException.class)
    public void opprettGsakOppgave__skal_kaste_bad_request_exception_hvis_gsak_returnerer_bad_request() {
        mockReturverdiFraGsak(gsakResponseEntity(HttpStatus.BAD_REQUEST));
        gsakKlient.opprettGsakOppgave(gsakRequest());
    }

    private void captureGsakRequest() {
        verify(restTemplate, times(1)).postForEntity(anyString(), requestCaptor.capture(), eq(String.class));
    }

    private void mockReturverdiFraGsak(ResponseEntity<String> gsakEntity) {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(gsakEntity);
    }
}
