package no.nav.arbeidsgiver.kontakt.oss.testUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndeling.KommuneEllerBydel;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndeling.FylkesinndelingRepository;
import org.springframework.stereotype.Component;

import java.util.List;

import static no.nav.arbeidsgiver.kontakt.oss.testUtils.TestData.lesFil;

@Slf4j
@Component
public class DatabasePopulator {
    private final FylkesinndelingRepository kommuneRepository;

    private static final String KOMMUNER_OG_BYDELER_JSON = lesFil("mock/kommunerOgBydeler.json");

    public DatabasePopulator(FylkesinndelingRepository kommuneRepository) {
        this.kommuneRepository = kommuneRepository;
    }

    @SneakyThrows
    public void populerFylkesinndelingRepositoryForÅUnngåNullpointers() {
        ObjectMapper mapper = new ObjectMapper();
        List<KommuneEllerBydel> kommunerOfBydeler = mapper.readValue(KOMMUNER_OG_BYDELER_JSON, new TypeReference<List<KommuneEllerBydel>>() {
        });
        kommuneRepository.oppdatereFylkesinndelinger(kommunerOfBydeler);
        log.info("FylkesinndelingRepository populert med testdata.");
    }

}
