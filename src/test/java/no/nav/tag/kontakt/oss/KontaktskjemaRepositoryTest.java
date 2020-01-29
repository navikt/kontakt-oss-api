package no.nav.tag.kontakt.oss;

import no.nav.tag.kontakt.oss.gsak.GsakOppgave;
import no.nav.tag.kontakt.oss.gsak.GsakOppgave.OppgaveStatus;
import no.nav.tag.kontakt.oss.gsak.GsakOppgaveRepository;
import no.nav.tag.kontakt.oss.salesforce.utsending.KontaktskjemaUtsending;
import no.nav.tag.kontakt.oss.salesforce.utsending.KontaktskjemaUtsendingRepository;
import no.nav.tag.kontakt.oss.testUtils.TestData;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"mock.enabled=false"})
public class KontaktskjemaRepositoryTest {

    @Autowired
    private KontaktskjemaRepository kontaktskjemaRepository;

    @Autowired
    private KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository;

    @Autowired
    private GsakOppgaveRepository oppgaveRepository;

    @After
    public void tearDown() {
        kontaktskjemaRepository.deleteAll();
        oppgaveRepository.deleteAll();
    }

    @Test
    public void skalLagre() {
        kontaktskjemaRepository.save(TestData.kontaktskjema());
    }

    @Test
    public void skalLagreOgHenteUt() {
        Kontaktskjema lagretSkjema = kontaktskjemaRepository.save(TestData.kontaktskjema());

        assertThat(kontaktskjemaRepository.findById(lagretSkjema.getId()).isPresent(), is(true));
    }

    @Test(expected = DbActionExecutionException.class)
    public void skalFeileHvisKommuneErForLang() {
        Kontaktskjema kontaktskjema = TestData.kontaktskjema();
        kontaktskjema.setKommunenr("1234567");
        kontaktskjemaRepository.save(kontaktskjema);
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

        assertThat(kontaktskjemaRepository.findAllNewerThan(now().minusDays(4)).size(), is(2));
        assertThat(kontaktskjemaRepository.findAllNewerThan(now().minusDays(2)).size(), is(1));
        assertThat(kontaktskjemaRepository.findAllNewerThan(now()).size(), is(0));
    }

    private Kontaktskjema skjemaMedDato(LocalDateTime opprettetTidspunkt) {
        Kontaktskjema skjema1 = TestData.kontaktskjema();
        skjema1.setOpprettet(opprettetTidspunkt);
        return skjema1;
    }

    @Test
    public void skalHenteSkjemaSomIkkeHarGsakOppgave() {
        kontaktskjemaRepository.save(TestData.kontaktskjema());
        assertThat(kontaktskjemaRepository.findAllWithNoGsakOppgave().size(), is(1));
    }

    @Test
    public void skalIkkeHenteSkjemaDersomGsakOppgaveErOpprettet() {
        Kontaktskjema lagretSkjema = kontaktskjemaRepository.save(TestData.kontaktskjema());
        assertThat(kontaktskjemaRepository.findAllWithNoGsakOppgave().size(), is(1));
        oppgaveRepository.save(GsakOppgave.builder().kontaktskjemaId(lagretSkjema.getId()).status(OppgaveStatus.OK).build());
        assertThat(kontaktskjemaRepository.findAllWithNoGsakOppgave().size(), is(0));
    }

    @Test
    public void skalHenteSkjemaDersomGsakOppgaveHarFeilet() {
        Kontaktskjema lagretSkjema = kontaktskjemaRepository.save(TestData.kontaktskjema());
        assertThat(kontaktskjemaRepository.findAllWithNoGsakOppgave().size(), is(1));
        oppgaveRepository.save(GsakOppgave.builder().kontaktskjemaId(lagretSkjema.getId()).status(OppgaveStatus.FEILET).build());
        assertThat(kontaktskjemaRepository.findAllWithNoGsakOppgave().size(), is(1));
        oppgaveRepository.save(GsakOppgave.builder().kontaktskjemaId(lagretSkjema.getId()).status(OppgaveStatus.OK).build());
        assertThat(kontaktskjemaRepository.findAllWithNoGsakOppgave().size(), is(0));
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

        assertThat(kontaktskjemaRepository.hentKontakskjemaerSomSkalSendesTilSalesforce().size(), is(0));
    }

    @Test
    public void skalHenteSkjemaSomIkkeErSentTilSalesforce() {
        Kontaktskjema lagretSkjema = kontaktskjemaRepository.save(TestData.kontaktskjema());
        kontaktskjemaUtsendingRepository.save(KontaktskjemaUtsending.klarTilUtsending(lagretSkjema.getId(), now()));

        assertThat(kontaktskjemaRepository.hentKontakskjemaerSomSkalSendesTilSalesforce().size(), is(1));
    }

}



