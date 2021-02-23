package no.nav.arbeidsgiver.kontakt.oss.salesforce;

import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;
import no.nav.arbeidsgiver.kontakt.oss.TemaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SalesforceServiceTest {

    private SalesforceService salesforceService;

    @Mock
    private SalesforceKlient salesforceKlient;

    @BeforeEach
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
                false,
                null
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
                "1256456457",
                null
                        );

        salesforceService.sendKontaktskjemaTilSalesforce(kontaktskjema);

        verify(salesforceKlient, times(1)).sendContactFormTilSalesforce(eq(15), eq(ønsketContactForm));
    }
}
