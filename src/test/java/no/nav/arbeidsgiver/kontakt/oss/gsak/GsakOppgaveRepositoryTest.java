package no.nav.arbeidsgiver.kontakt.oss.gsak;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@SpringBootTest
@TestPropertySource(properties = {"mock.enabled=false"})
public class GsakOppgaveRepositoryTest {

    @Autowired
    private GsakOppgaveRepository repository;

    @AfterEach
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void skalLagreOgHenteUt() {
        GsakOppgave lagretOppgave = repository.save(GsakOppgave.builder().kontaktskjemaId(2).status(GsakOppgave.OppgaveStatus.OK).gsakId(5).opprettet(LocalDateTime.now()).build());

        GsakOppgave uthentetOppgave = repository.findById(lagretOppgave.getId()).get();
        assertThat(uthentetOppgave.getId(), greaterThan(0));
        assertThat(uthentetOppgave.getKontaktskjemaId(), is(2));
        assertThat(uthentetOppgave.getStatus(), Matchers.is(GsakOppgave.OppgaveStatus.OK));
        assertThat(uthentetOppgave.getGsakId(), is(5));
        assertThat(uthentetOppgave.getOpprettet(), not(nullValue()));
    }

}
