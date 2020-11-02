package no.nav.arbeidsgiver.kontakt.oss.salesforce;

import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;
import no.nav.arbeidsgiver.kontakt.oss.TemaType;
import no.nav.arbeidsgiver.kontakt.oss.featureToggles.FeatureToggleService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static no.nav.arbeidsgiver.kontakt.oss.testUtils.TestData.kontaktskjema;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SalesforceServiceTest {

    private SalesforceService salesforceService;

    @Mock
    private SalesforceKlient salesforceKlient;

    @Before
    public void setup() {
        salesforceService = new SalesforceService(salesforceKlient);
    }

    @Test
    public void sendKontaktskjemaTilSalesforce__skal_sende_med_riktig_contactForm() {
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
                "1000",
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
}
