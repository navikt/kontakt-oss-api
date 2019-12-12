package no.nav.tag.kontakt.oss.salesforce;

import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.TemaType;
import no.nav.tag.kontakt.oss.featureToggles.FeatureToggleService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static no.nav.tag.kontakt.oss.testUtils.TestData.kontaktskjemaBuilder;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SalesforceServiceTest {

    private SalesforceService salesforceService;

    @Mock
    private SalesforceKlient salesforceKlient;

    @Mock
    private FeatureToggleService featureToggles;

    @Before
    public void setup() {
        salesforceService = new SalesforceService(salesforceKlient, featureToggles);
    }

    @Test
    public void sendKontaktskjemaTilSalesforce__skal_sende_med_riktig_contactForm() {
        when(featureToggles.erEnabled(anyString())).thenReturn(true);

        Kontaktskjema kontaktskjema = new Kontaktskjema(
                15,
                LocalDateTime.now(),
                "1000",
                "Kommunenavn",
                "0101",
                "Min bedrift AS",
                "999999999",
                "Per",
                "Perssen",
                "per@per.no",
                "1256456457",
                "Rekruttering",
                TemaType.REKRUTTERING,
                false
        );

        ContactForm ønsketContactForm = new ContactForm(
                TemaType.REKRUTTERING,
                "0101",
                "Min bedrift AS",
                "999999999",
                "Per",
                "Perssen",
                "per@per.no",
                "1256456457"
        );

        salesforceService.sendKontaktskjemaTilSalesforce(kontaktskjema);

        verify(salesforceKlient, times(1)).sendContactFormTilSalesforce(eq(ønsketContactForm));
    }

    @Test
    public void sendKontaktskjemaTilSalesforce__skal_ikke_sende_kontaktskjema_hvis_fylke_ikke_er_pilot() {
        when(featureToggles.erEnabled(anyString())).thenReturn(true);

        salesforceService.sendKontaktskjemaTilSalesforce(
                kontaktskjemaBuilder().fylke("1234").build()
        );

        verify(salesforceKlient, times(0)).sendContactFormTilSalesforce(any());
    }

    @Test
    public void sendKontaktskjemaTilSalesforce__skal_ikke_sende_kontaktskjema_hvis_toggle_er_avskrudd() {
        when(featureToggles.erEnabled(anyString())).thenReturn(false);

        salesforceService.sendKontaktskjemaTilSalesforce(
                kontaktskjemaBuilder().fylke("1000").build()
        );

        verify(salesforceKlient, times(0)).sendContactFormTilSalesforce(any());
    }
}