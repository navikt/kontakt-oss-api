package no.nav.arbeidsgiver.kontakt.oss.kafka.utsending;

import no.nav.arbeidsgiver.kontakt.oss.kafka.utsending.KontaktskjemaUtsending;
import no.nav.arbeidsgiver.kontakt.oss.kafka.utsending.KontaktskjemaUtsendingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(kontaktskjemaUtsending).isNotNull();
    }

}
