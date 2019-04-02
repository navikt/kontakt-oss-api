package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import no.nav.tag.kontakt.oss.KontaktskjemaApplication;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Map;

import static no.nav.tag.kontakt.oss.TestData.*;
import static no.nav.tag.kontakt.oss.TestData.fraKommuneNrTilNavEnhet;
import static org.assertj.core.api.Assertions.assertThat;

// @RunWith(SpringRunner.class)
// @SpringBootTest
@TestPropertySource(properties = {"mock.enabled=false"})
public class FylkesinndelingRepositoryTest {

    FylkesinndelingRepository repository;

    @Ignore
    @Test
    public void repository__skal_gi_ut_det_som_blir_satt_inn() {
        FylkesinndelingMedNavEnheter fraFylkesenheterTilKommuner = fraFylkesenheterTilKommuner();
        Map<String, NavEnhet> fraKommuneNrTilNavEnhet = fraKommuneNrTilNavEnhet();

        repository.oppdaterInformasjonFraNorg(
                fraFylkesenheterTilKommuner,
                fraKommuneNrTilNavEnhet
        );

        assertThat(repository.hentFylkesinndeling().getFylkeTilKommuneEllerBydel())
                .isEqualTo(fraFylkesenheterTilKommuner.getFylkeTilKommuneEllerBydel());
        assertThat(repository.hentKommuneNrEllerBydelNrTilNavEnhet()).isEqualTo(fraKommuneNrTilNavEnhet);
    }

    @Ignore
    @Test
    public void repository__skal_sette_sistOppdatert() {
        repository.oppdaterInformasjonFraNorg(fraFylkesenheterTilKommuner(), fraKommuneNrTilNavEnhet());
        assertThat(repository.hentSistOppdatert()).isAfter(LocalDateTime.now().minusSeconds(10));
    }

}