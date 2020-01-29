package no.nav.tag.kontakt.oss.salesforce.utsending;

import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.KontaktskjemaRepository;
import no.nav.tag.kontakt.oss.salesforce.SalesforceException;
import no.nav.tag.kontakt.oss.salesforce.SalesforceService;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static java.time.LocalDateTime.now;
import static no.nav.tag.kontakt.oss.testUtils.TestData.kontaktskjemaBuilder;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"mock.enabled=false"})
public class KontaktskjemaUtsendingServiceTest {

    @Autowired
    private KontaktskjemaRepository kontaktskjemaRepository;

    @Autowired
    private KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository;

    @MockBean
    SalesforceService salesforceService;

    @Autowired
    private KontaktskjemaUtsendingService kontaktskjemaUtsendingService;


    @After
    public void cleanUp() {
        kontaktskjemaRepository.deleteAll();
        kontaktskjemaUtsendingRepository.deleteAll();
    }

    @Test
    public void sendSkjemaTilSalesForce_skal_oppdatere_status_til_SENT() {
        Kontaktskjema kontaktskjema = opprettOgHentKontaktskjema();
        kontaktskjemaUtsendingRepository.save(KontaktskjemaUtsending.klarTilUtsending(kontaktskjema.getId(), now()));
        sjekkSkjemaErLagretOgKlartTilUtsending(kontaktskjema.getId());

        kontaktskjemaUtsendingService.sendSkjemaTilSalesForce(kontaktskjema);

        KontaktskjemaUtsending kontaktskjemaUtsending = kontaktskjemaUtsendingRepository.findAll().iterator().next();
        assertTrue(kontaktskjemaUtsending.erSent());
    }

    @Test
    public void sendSkjemaTilSalesForce_skal_IKKE_oppdatere_status_til_SENT_dersom_utsending_til_Salesforce_feiler() {
        doThrow(new SalesforceException("Kunne ikke sende skjema til SF"))
                .when(salesforceService)
                .sendKontaktskjemaTilSalesforce(any(Kontaktskjema.class));
        Kontaktskjema kontaktskjema = opprettOgHentKontaktskjema();
        kontaktskjemaUtsendingRepository.save(KontaktskjemaUtsending.klarTilUtsending(kontaktskjema.getId(), now()));
        sjekkSkjemaErLagretOgKlartTilUtsending(kontaktskjema.getId());

        try {
            kontaktskjemaUtsendingService.sendSkjemaTilSalesForce(kontaktskjema);
            fail("SalesforceException var forventet her");
        } catch (SalesforceException e) { /* Suksess */ }

        KontaktskjemaUtsending kontaktskjemaUtsending = kontaktskjemaUtsendingRepository.findAll().iterator().next();
        assertFalse(kontaktskjemaUtsending.erSent());
    }


    private void sjekkSkjemaErLagretOgKlartTilUtsending(Integer kontaktskjemaId) {
        KontaktskjemaUtsending kontaktskjemaUtsendingBeforeSending = kontaktskjemaUtsendingRepository.hentKontakskjemaUtsending(kontaktskjemaId);
        assertFalse(kontaktskjemaUtsendingBeforeSending.erSent());
    }

    private Kontaktskjema opprettOgHentKontaktskjema() {
        kontaktskjemaRepository.save(kontaktskjemaBuilder().build());
        Iterable<Kontaktskjema> alleKontaktskjemaer = kontaktskjemaRepository.findAll();
        return alleKontaktskjemaer.iterator().next();
    }
}