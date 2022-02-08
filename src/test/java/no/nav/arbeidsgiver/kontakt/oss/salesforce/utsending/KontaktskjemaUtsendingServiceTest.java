package no.nav.arbeidsgiver.kontakt.oss.salesforce.utsending;

import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;
import no.nav.arbeidsgiver.kontakt.oss.KontaktskjemaRepository;
import no.nav.arbeidsgiver.kontakt.oss.utsending.KontaktskjemaUtsending;
import no.nav.arbeidsgiver.kontakt.oss.utsending.KontaktskjemaUtsendingRepository;
import no.nav.arbeidsgiver.kontakt.oss.utsending.KontaktskjemaUtsendingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static java.time.LocalDateTime.now;
import static no.nav.arbeidsgiver.kontakt.oss.testUtils.TestData.kontaktskjemaBuilder;
import static org.assertj.core.api.Assertions.assertThat;



@SpringBootTest
@ActiveProfiles({"local"})
@TestPropertySource(properties = {"mock.enabled=false"})
public class KontaktskjemaUtsendingServiceTest {

    @Autowired
    private KontaktskjemaRepository kontaktskjemaRepository;

    @Autowired
    private KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository;

    @Autowired
    private KontaktskjemaUtsendingService kontaktskjemaUtsendingService;


    @BeforeEach
    public void setUp() {
        cleanUpDb();
    }

    @AfterEach
    public void tearDown() {
        cleanUpDb();
    }

    @Test
    public void sendSkjemaTilSalesForce_skal_oppdatere_status_til_SENT() {
        Kontaktskjema kontaktskjema = opprettOgHentKontaktskjema();
        kontaktskjemaUtsendingRepository.save(KontaktskjemaUtsending.klarTilUtsending(kontaktskjema.getId(), now()));
        sjekkSkjemaErLagretOgKlartTilUtsending(kontaktskjema.getId());

        kontaktskjemaUtsendingService.sendSkjemaTilKafka(kontaktskjema);

        KontaktskjemaUtsending kontaktskjemaUtsending = kontaktskjemaUtsendingRepository.findAll().iterator().next();
        assertThat(kontaktskjemaUtsending.erSent()).isTrue();
    }

    private void sjekkSkjemaErLagretOgKlartTilUtsending(Integer kontaktskjemaId) {
        KontaktskjemaUtsending kontaktskjemaUtsendingBeforeSending = kontaktskjemaUtsendingRepository.hentKontakskjemaUtsending(kontaktskjemaId);
        assertThat(kontaktskjemaUtsendingBeforeSending.erSent()).isFalse();
    }

    private Kontaktskjema opprettOgHentKontaktskjema() {
        kontaktskjemaRepository.save(kontaktskjemaBuilder().build());
        Iterable<Kontaktskjema> alleKontaktskjemaer = kontaktskjemaRepository.findAll();
        return alleKontaktskjemaer.iterator().next();
    }

    private void cleanUpDb() {
        kontaktskjemaRepository.deleteAll();
        kontaktskjemaUtsendingRepository.deleteAll();
    }
}
