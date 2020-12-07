package no.nav.arbeidsgiver.kontakt.oss.salesforce.utsending;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static java.time.LocalDateTime.now;
import static org.junit.Assert.assertNotNull;

@SpringBootTest
@TestPropertySource(properties = {"mock.enabled=false"})
public class KontaktskjemaUtsendingRepositoryTest {

    @Autowired
    private KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository;

    @AfterEach
    public void tearDown() {
        kontaktskjemaUtsendingRepository.deleteAll();
    }

    @Test
    public void skalHenteBasertPåKontaktskjemaId() {
        kontaktskjemaUtsendingRepository.save(KontaktskjemaUtsending.klarTilUtsending(123, now()));

        KontaktskjemaUtsending kontaktskjemaUtsending = kontaktskjemaUtsendingRepository.hentKontakskjemaUtsending(123);
        assertNotNull(kontaktskjemaUtsending);
    }

}
