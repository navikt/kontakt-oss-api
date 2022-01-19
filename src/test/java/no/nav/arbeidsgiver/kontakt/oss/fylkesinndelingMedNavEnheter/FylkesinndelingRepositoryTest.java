package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static no.nav.arbeidsgiver.kontakt.oss.testUtils.TestData.fraFylkesenheterTilKommuner;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"local"})
@TestPropertySource(properties = {"mock.enabled=false"})
@Slf4j
public class FylkesinndelingRepositoryTest {

    @Autowired
    FylkesinndelingRepository repository;

    @Test
    public void repository__skal_gi_ut_det_som_blir_satt_inn() {
        Map<String, List<KommuneEllerBydel>> fraFylkesenheterTilKommuner = fraFylkesenheterTilKommuner();

        repository.oppdaterInformasjonFraNorg(
                fraFylkesenheterTilKommuner
        );

        assertThat(repository.hentFylkesinndeling())
                .isEqualTo(fraFylkesenheterTilKommuner);
    }

    @Test
    public void repository__skal_sette_sistOppdatert() {
        repository.oppdaterInformasjonFraNorg(fraFylkesenheterTilKommuner());
        assertThat(repository.hentSistOppdatert()).isAfter(LocalDateTime.now().minusSeconds(10));
    }

}
