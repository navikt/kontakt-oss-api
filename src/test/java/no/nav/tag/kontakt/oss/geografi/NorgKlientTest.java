package no.nav.tag.kontakt.oss.geografi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.tag.kontakt.oss.KontaktskjemaException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NorgKlientTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RestTemplate restTemplate;

    private NorgKlient norgKlient;

    @Before
    public void setUp() {
        norgKlient = new NorgKlient(restTemplate, "");
    }

    @Test
    public void hentGeografiFraNorg__skal_oversette_til_riktig_objekt() throws JsonProcessingException {
        NorgGeografi geografi = new NorgGeografi("navn", "term");

        ResponseEntity<String> responseEntity = new ResponseEntity<>(
                objectMapper.writeValueAsString(Collections.singletonList(geografi)),
                HttpStatus.OK
        );
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);
        assertThat(norgKlient.hentGeografiFraNorg()).isEqualTo(new Geografi(Collections.singletonList(geografi)));
    }

    @Test(expected = KontaktskjemaException.class)
    public void hentGeografiFraNorg__skal_feile_hvis_respons_ikke_returnerer_ok() {
        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);
        norgKlient.hentGeografiFraNorg();
    }

    @Test(expected = KontaktskjemaException.class)
    public void hentGeografiFraNorg__skal_feile_hvis_respons_ikke_returnerer_gyldig_json() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("{ikke gyldig json}", HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);
        norgKlient.hentGeografiFraNorg();
    }
}