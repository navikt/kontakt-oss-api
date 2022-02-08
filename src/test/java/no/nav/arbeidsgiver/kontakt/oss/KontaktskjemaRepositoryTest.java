package no.nav.arbeidsgiver.kontakt.oss;

import no.nav.arbeidsgiver.kontakt.oss.utsending.KontaktskjemaUtsending;
import no.nav.arbeidsgiver.kontakt.oss.utsending.KontaktskjemaUtsendingRepository;
import no.nav.arbeidsgiver.kontakt.oss.testUtils.TestData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("local")
@TestPropertySource(properties = {"mock.enabled=false"})
public class KontaktskjemaRepositoryTest {

    @Autowired
    private KontaktskjemaRepository kontaktskjemaRepository;

    @Autowired
    private KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository;


    @AfterEach
    public void tearDown() {
        kontaktskjemaRepository.deleteAll();
    }

    @Test
    public void skalLagre() {
        kontaktskjemaRepository.save(TestData.kontaktskjema());
    }

    @Test
    public void skalLagreOgHenteUt() {
        Kontaktskjema lagretSkjema = kontaktskjemaRepository.save(TestData.kontaktskjema());

        assertThat(kontaktskjemaRepository.findById(lagretSkjema.getId())).isPresent();
    }

    @Test
    public void skalFeileHvisKommuneErForLang() {
        Kontaktskjema kontaktskjema = TestData.kontaktskjema();
        kontaktskjema.setKommunenr("1234567");
        assertThrows(DbActionExecutionException.class, () -> kontaktskjemaRepository.save(kontaktskjema));
    }

    @Test
    public void skalKunneLagreSkjemaMedBydelsnr() {
        Kontaktskjema kontaktskjema = TestData.kontaktskjema();
        kontaktskjema.setKommunenr("123456");
        kontaktskjemaRepository.save(kontaktskjema);
    }

    @Test
    public void skalHenteBasertPaDato() {
        kontaktskjemaRepository.save(skjemaMedDato(now().minusDays(3)));
        kontaktskjemaRepository.save(skjemaMedDato(now().minusDays(1)));

        assertThat(kontaktskjemaRepository.findAllNewerThan(now().minusDays(4)).size()).isEqualTo(2);

        assertThat(kontaktskjemaRepository.findAllNewerThan(now().minusDays(2)).size()).isEqualTo(1);
        assertThat(kontaktskjemaRepository.findAllNewerThan(now()).size()).isEqualTo(0);
    }

    private Kontaktskjema skjemaMedDato(LocalDateTime opprettetTidspunkt) {
        Kontaktskjema skjema1 = TestData.kontaktskjema();
        skjema1.setOpprettet(opprettetTidspunkt);
        return skjema1;
    }

    @Test
    public void skalIkkeHenteSkjemaDersomKontaktskjemaAlleredeSentTilSalesforce() {
        Kontaktskjema lagretSkjema = kontaktskjemaRepository.save(TestData.kontaktskjema());
        kontaktskjemaUtsendingRepository.save(
                KontaktskjemaUtsending.nyKontaktskjemaUtsending(
                        lagretSkjema.getId(),
                        now(),
                        KontaktskjemaUtsending.UtsendingStatus.SENT
                )
        );

        assertThat(kontaktskjemaRepository.hentKontakskjemaerSomSkalSendesTilKafka().size()).isEqualTo(0);
    }

    @Test
    public void skalHenteSkjemaSomIkkeErSentTilSalesforce() {
        Kontaktskjema lagretSkjema = kontaktskjemaRepository.save(TestData.kontaktskjema());
        kontaktskjemaUtsendingRepository.save(KontaktskjemaUtsending.klarTilUtsending(lagretSkjema.getId(), now()));

        assertThat(kontaktskjemaRepository.hentKontakskjemaerSomSkalSendesTilKafka().size()).isEqualTo(1);
    }

}



