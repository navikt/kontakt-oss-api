package no.nav.tag.kontaktskjema.gsak;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GsakSchedulerTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void skalFeileVedLagringAvKontaktskjemaMedForhandsdefinertId() throws InterruptedException {
        Thread.sleep(1000);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM SHEDLOCK", Integer.class), is(1));
    }

}
