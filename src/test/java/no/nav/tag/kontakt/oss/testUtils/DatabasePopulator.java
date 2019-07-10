package no.nav.tag.kontakt.oss.testUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.FylkesinndelingMedNavEnheter;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.FylkesinndelingRepository;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.KommuneEllerBydel;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.NavEnhet;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static no.nav.tag.kontakt.oss.testUtils.TestData.lesFil;

@Slf4j
@Component("databasePopulator")
public class DatabasePopulator {
    private final FylkesinndelingRepository fylkesinndelingRepository;

    private static final String MAP_FRA_KOMMUNE_TIL_NAVENHET_JSON = lesFil("mock/mapFraKommuneTilNavEnhet.json");
    private static final String FYLKESINNDELING_JSON = lesFil("mock/fylkesinndeling.json");

    public DatabasePopulator(FylkesinndelingRepository fylkesinndelingRepository) {
        this.fylkesinndelingRepository = fylkesinndelingRepository;
    }

    @SneakyThrows
    public void populerFylkesinndelingRepositoryHvisTomForÅUnngåNullpointers() {
        try {
            FylkesinndelingMedNavEnheter hei = fylkesinndelingRepository.hentFylkesinndeling();
            log.info("FylkesinndelingRepository allerede populert.");
            log.info(hei.toString());
        } catch (NullPointerException e) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, NavEnhet> norgInfo = mapper.readValue(MAP_FRA_KOMMUNE_TIL_NAVENHET_JSON, new TypeReference<Map<String, NavEnhet>>() {});
            FylkesinndelingMedNavEnheter fylkesinndeling = new FylkesinndelingMedNavEnheter(
                    mapper.readValue(FYLKESINNDELING_JSON, new TypeReference<Map<String, List<KommuneEllerBydel>>>() {})
            );
            fylkesinndelingRepository.oppdaterInformasjonFraNorg(fylkesinndeling, norgInfo);
            log.info("FylkesinndelingRepository populert med testdata.");
        }
    }
}
