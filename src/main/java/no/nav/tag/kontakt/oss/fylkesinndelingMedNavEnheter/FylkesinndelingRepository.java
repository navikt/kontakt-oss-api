package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.LocalDate.now;

@Component
public class FylkesinndelingRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public FylkesinndelingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @SneakyThrows
    public Map<String, NavEnhet> hentKommuneNrEllerBydelNrTilNavEnhet() {
        String json = jdbcTemplate.queryForObject("select mapFraKommunerOgBydelerTilNavEnheter from NORG_MAPPING", String.class);
        return objectMapper.readValue(json, new TypeReference<Map<String, NavEnhet>>() {});
    }

    @SneakyThrows
    public FylkesinndelingMedNavEnheter hentFylkesinndeling() {
        String json = jdbcTemplate.queryForObject("select mapFraFylkesenheterTilKommunerOgBydeler from NORG_MAPPING", String.class);
        return new FylkesinndelingMedNavEnheter(objectMapper.readValue(json, new TypeReference<Map<String, List<KommuneEllerBydel>>>() {}));
    }

    @SneakyThrows
    public void oppdaterInformasjonFraNorg(
            FylkesinndelingMedNavEnheter fylkesinndeling,
            Map<String, NavEnhet> kommuneNrEllerBydelNrTilNavEnhet
    ) {
        jdbcTemplate.update(
                "insert into NORG_MAPPING values (?, ?, ?)",
                now(),
                objectMapper.writeValueAsString(fylkesinndeling.getMapFraFylkesenheterTilKommunerOgBydeler()),
                objectMapper.writeValueAsString(kommuneNrEllerBydelNrTilNavEnhet)
        );
    }
}
