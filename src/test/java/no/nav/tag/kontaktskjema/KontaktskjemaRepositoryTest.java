package no.nav.tag.kontaktskjema;

import static no.nav.tag.kontaktskjema.TestData.lagKontaktskjema;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KontaktskjemaRepositoryTest {

    @Autowired
    private KontaktskjemaRepository kontaktskjemaRepository;

    @Test
    public void skalHenteBasertPaDato() {
        Kontaktskjema skjema1 = lagKontaktskjema();
        skjema1.setOpprettet(LocalDateTime.now().minusDays(3));

        Kontaktskjema skjema2 = lagKontaktskjema();
        skjema2.setOpprettet(LocalDateTime.now().minusDays(1));
        kontaktskjemaRepository.save(skjema1);
        kontaktskjemaRepository.save(skjema2);
        
        assertThat(kontaktskjemaRepository.findAllNewerThan(LocalDateTime.now().minusDays(4)).size(), is(2));
        assertThat(kontaktskjemaRepository.findAllNewerThan(LocalDateTime.now().minusDays(2)).size(), is(1));
        assertThat(kontaktskjemaRepository.findAllNewerThan(LocalDateTime.now()).size(), is(0));
    }

}
