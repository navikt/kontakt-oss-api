package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FylkesinndelingRepository {
    private static final TypeReference<Map<String, List<KommuneEllerBydel>>> FYLKESINNDELING_TYPE = new TypeReference<>() {
    };

    private static final TypeReference<Map<String, NavEnhet>> KOMMUNE_TIL_ENHET_TYPE = new TypeReference<>() {
    };

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final JdbcTemplate jdbcTemplate;


    public FylkesinndelingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public LocalDateTime hentSistOppdatert() {
        return jdbcTemplate.queryForObject("SELECT sistOppdatert FROM norg_mapping", LocalDateTime.class);
    }

    public List<KommuneEllerBydel> alleLokasjoner() {
        return hentFylkesinndeling()
                .values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public void oppdaterInformasjonFraNorg(
            Map<String, List<KommuneEllerBydel>> fylkesenhetsnrTilKommunerOgBydeler,
            Map<String, NavEnhet> kommuneNrEllerBydelNrTilNavEnhet
    ) {
        jdbcTemplate.update(
                "UPDATE norg_mapping SET sistOppdatert=?, mapFraFylkesenheterTilKommunerOgBydeler=?, mapFraKommunerOgBydelerTilNavEnheter=?",
                LocalDateTime.now(),
                objectMapper.writeValueAsString(fylkesenhetsnrTilKommunerOgBydeler),
                objectMapper.writeValueAsString(kommuneNrEllerBydelNrTilNavEnhet)
        );
    }

    @SneakyThrows
    @Deprecated
    public Map<String, NavEnhet> hentKommuneNrEllerBydelNrTilNavEnhet() {
        String json = jdbcTemplate.queryForObject("SELECT mapFraKommunerOgBydelerTilNavEnheter FROM norg_mapping", String.class);
        return objectMapper.readValue(json, KOMMUNE_TIL_ENHET_TYPE);
    }

    @SneakyThrows
    @Deprecated
    public Map<String, List<KommuneEllerBydel>> hentFylkesinndeling() {
        String json = jdbcTemplate.queryForObject("SELECT mapFraFylkesenheterTilKommunerOgBydeler FROM norg_mapping", String.class);
        return objectMapper.readValue(json, FYLKESINNDELING_TYPE);
    }
}
