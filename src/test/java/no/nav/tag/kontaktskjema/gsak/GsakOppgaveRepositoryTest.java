package no.nav.tag.kontaktskjema.gsak;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

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

    @Test
    public void skalLagreOgHenteUt() {
        GsakOppgave lagretOppgave = repository.save(GsakOppgave.builder().kontaktskjemaId(1).build());
        
        assertThat(repository.findById(lagretOppgave.getId()).isPresent(), is(true));
    }

}
