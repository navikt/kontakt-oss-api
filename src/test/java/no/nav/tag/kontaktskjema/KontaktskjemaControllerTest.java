package no.nav.tag.kontaktskjema;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static no.nav.tag.kontaktskjema.TestData.lagKontaktskjema;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KontaktskjemaControllerTest {

    @Autowired
    KontaktskjemaController kontaktskjemaController;

    @Test
    public void skalFeileVedLagringAvKontaktskjemaMedForhandsdefinertId() {
        kontaktskjemaController.meldInteresse(lagKontaktskjema());
    }
}