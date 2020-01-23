package no.nav.tag.kontakt.oss.salesforce;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static no.nav.tag.kontakt.oss.testUtils.TestData.contactForm;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SalesforceKlientTest {
    private SalesforceKlient salesforceKlient;

    @Mock
    private RestTemplate restTemplate;

    private final static String authUrl = "authUrl";
    private final static String apiUrl = "apiUrl";

    @Before
    public void setUp() {
        salesforceKlient = new SalesforceKlient(
                restTemplate, authUrl, apiUrl, "", "", "", ""
        );
    }

    @Test
    public void sendKontaktskjemaTilSalesforce__skal_hente_autoriseringstoken() {
        mockAuthKall(new ResponseEntity<>(new SalesforceToken("token"), HttpStatus.OK));
        mockApiKall(new ResponseEntity(HttpStatus.OK));

        salesforceKlient.sendContactFormTilSalesforce(contactForm());

        verify(restTemplate, times(1))
                .exchange(eq(authUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(SalesforceToken.class));
    }

    @Test(expected = SalesforceException.class)
    public void sendKontaktskjemaTilSalesforce__skal_kaste_exception_hvis_resultatet_ikke_gir_200() {
        mockAuthKall(new ResponseEntity<>(new SalesforceToken("token"), HttpStatus.OK));
        mockApiKall(new ResponseEntity(HttpStatus.NOT_FOUND));
        salesforceKlient.sendContactFormTilSalesforce(contactForm());
    }

    @Test(expected = SalesforceException.class)
    public void hentSalesforceToken__skal_kaste_exception_hvis_resultatet_ikke_gir_200() {
        mockAuthKall(new ResponseEntity(HttpStatus.BAD_REQUEST));
        salesforceKlient.hentSalesforceToken();
    }

    private void mockApiKall(ResponseEntity response) {
        when(restTemplate.exchange(eq(apiUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);
    }

    private void mockAuthKall(ResponseEntity response) {
        when(restTemplate.exchange(eq(authUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(SalesforceToken.class)))
                .thenReturn(response);
    }
}