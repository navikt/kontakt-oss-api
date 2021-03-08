package no.nav.arbeidsgiver.kontakt.oss.salesforce;

import no.nav.arbeidsgiver.kontakt.oss.salesforce.klient.SalesforceException;
import no.nav.arbeidsgiver.kontakt.oss.salesforce.klient.SalesforceKlient;
import no.nav.arbeidsgiver.kontakt.oss.salesforce.klient.SalesforceToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static no.nav.arbeidsgiver.kontakt.oss.testUtils.TestData.contactForm;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SalesforceKlientTest {
    private SalesforceKlient salesforceKlient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    private final static String authUrl = "authUrl";
    private final static String apiUrl = "apiUrl";

    @BeforeEach
    public void setUp() {
        when(restTemplateBuilder.errorHandler(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        salesforceKlient = new SalesforceKlient(
                restTemplateBuilder, authUrl, apiUrl, "", "", "", ""
        );
    }

    @Test
    public void sendKontaktskjemaTilSalesforce__skal_hente_autoriseringstoken() {
        mockAuthKall(new ResponseEntity<>(new SalesforceToken("token"), HttpStatus.OK));
        mockApiKall(new ResponseEntity<>(HttpStatus.OK));

        salesforceKlient.sendContactFormTilSalesforce(1111, contactForm());

        verify(restTemplate, times(1))
                .exchange(eq(authUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(SalesforceToken.class));
    }

    @Test
    public void sendKontaktskjemaTilSalesforce__skal_kaste_exception_hvis_resultatet_ikke_gir_200() {
        mockAuthKall(new ResponseEntity<>(new SalesforceToken("token"), HttpStatus.OK));
        mockApiKall(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        assertThrows(SalesforceException.class, () -> salesforceKlient.sendContactFormTilSalesforce(1111, contactForm()));
    }

    @Test
    public void hentSalesforceToken__skal_kaste_exception_hvis_resultatet_ikke_gir_200() {
        mockAuthKall(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        assertThrows(SalesforceException.class, () -> salesforceKlient.hentSalesforceToken());
    }

    private void mockApiKall(ResponseEntity<String> response) {
        when(restTemplate.exchange(eq(apiUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);
    }

    private void mockAuthKall(ResponseEntity<SalesforceToken> response) {
        when(restTemplate.exchange(eq(authUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(SalesforceToken.class)))
                .thenReturn(response);
    }
}
