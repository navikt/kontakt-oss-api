package no.nav.tag.kontakt.oss.salesforce.utsending;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static java.time.LocalDateTime.now;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"mock.enabled=false"})
public class KontaktskjemaUtsendingRepositoryTest {

    @Autowired
    private KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository;

    @After
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