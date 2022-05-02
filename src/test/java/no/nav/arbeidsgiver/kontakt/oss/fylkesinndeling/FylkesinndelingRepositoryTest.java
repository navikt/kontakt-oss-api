package no.nav.arbeidsgiver.kontakt.oss.fylkesinndeling;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static no.nav.arbeidsgiver.kontakt.oss.testUtils.TestData.hentTestKommuner;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"local"})
@TestPropertySource(properties = {"mock.enabled=false"})
@Slf4j
public class FylkesinndelingRepositoryTest {

    @Autowired
    private FylkesinndelingRepository repository;

    @Test
    @SneakyThrows
    public void repository__skal_gi_ut_det_som_blir_satt_inn() {
        List<KommuneEllerBydel> kommuner = hentTestKommuner();

        repository.oppdatereFylkesinndelinger(kommuner);

        assertThat(repository.hentFylkesinndelinger().size() == kommuner.size());
    }

}
