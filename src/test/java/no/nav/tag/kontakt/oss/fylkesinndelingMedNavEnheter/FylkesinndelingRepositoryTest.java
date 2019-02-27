package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static no.nav.tag.kontakt.oss.TestData.*;
import static no.nav.tag.kontakt.oss.TestData.fraKommuneNrTilNavEnhet;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FylkesinndelingRepositoryTest {

    @Autowired FylkesinndelingRepository repository;

    @Test
    public void repository__skal_gi_ut_det_som_blir_satt_inn() {
        FylkesinndelingMedNavEnheter fraFylkesenheterTilKommuner = fraFylkesenheterTilKommuner();
        Map<String, NavEnhet> fraKommuneNrTilNavEnhet = fraKommuneNrTilNavEnhet();

        repository.oppdaterInformasjonFraNorg(
                fraFylkesenheterTilKommuner,
                fraKommuneNrTilNavEnhet
        );

        assertThat(repository.hentFylkesinndeling().getMapFraFylkesenheterTilKommunerOgBydeler())
                .isEqualTo(fraFylkesenheterTilKommuner.getMapFraFylkesenheterTilKommunerOgBydeler());
        assertThat(repository.hentKommuneNrEllerBydelNrTilNavEnhet()).isEqualTo(fraKommuneNrTilNavEnhet);
    }

}