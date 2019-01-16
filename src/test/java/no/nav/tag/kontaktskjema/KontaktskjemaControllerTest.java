package no.nav.tag.kontaktskjema;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static no.nav.tag.kontaktskjema.TestData.lagKontaktskjema;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KontaktskjemaControllerTest {

    @Autowired
    KontaktskjemaController kontaktskjemaController;

    @Test(expected = KontaktskjemaException.class)
    public void skalFeileVedLagringAvKontaktskjemaMedForhandsdefinertId() {
        Kontaktskjema kontaktskjema = lagKontaktskjema();
        kontaktskjema.setId(52);
        kontaktskjemaController.meldInteresse(kontaktskjema);
    }

    @Test
    public void skalReturnere429VedForMangeInnsendinger() {
        for (int i=0; i<10; i++) {
            kontaktskjemaController.meldInteresse(lagKontaktskjema());
        }

        ResponseEntity result = kontaktskjemaController.meldInteresse(lagKontaktskjema());
        assertThat(result.getStatusCode(), is(HttpStatus.TOO_MANY_REQUESTS));
    }
}