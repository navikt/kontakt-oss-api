package no.nav.tag.kontaktskjema.gsak;

import static no.nav.tag.kontaktskjema.gsak.GsakOppgave.OppgaveStatus.OK;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GsakOppgaveRepositoryTest {

    @Autowired
    private GsakOppgaveRepository repository;

    @After
    public void tearDown() {
        repository.deleteAll();
    }
    
    @Test
    public void skalLagreOgHenteUt() {
        GsakOppgave lagretOppgave = repository.save(GsakOppgave.builder().kontaktskjemaId(2).status(OK).gsakId(5).opprettet(LocalDateTime.now()).build());
        
        GsakOppgave uthentetOppgave = repository.findById(lagretOppgave.getId()).get();
        assertThat(uthentetOppgave.getId(), greaterThan(0));
        assertThat(uthentetOppgave.getKontaktskjemaId(), is(2));
        assertThat(uthentetOppgave.getStatus(), is(OK));
        assertThat(uthentetOppgave.getGsakId(), is(5));
        assertThat(uthentetOppgave.getOpprettet(), not(nullValue()));
    }

}
