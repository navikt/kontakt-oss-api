package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class FylkesinndelingRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public FylkesinndelingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public LocalDateTime hentSistOppdatert() {
        return jdbcTemplate.queryForObject("SELECT sistOppdatert FROM norg_mapping", LocalDateTime.class);
    }

    @SneakyThrows
    public Map<String, NavEnhet> hentKommuneNrEllerBydelNrTilNavEnhet() {
        String json = jdbcTemplate.queryForObject("SELECT mapFraKommunerOgBydelerTilNavEnheter FROM norg_mapping", String.class);
        return objectMapper.readValue(json, new TypeReference<Map<String, NavEnhet>>() {
        });
    }

    @SneakyThrows
    public FylkesinndelingMedNavEnheter hentFylkesinndeling() {
        String json = jdbcTemplate.queryForObject("SELECT mapFraFylkesenheterTilKommunerOgBydeler FROM norg_mapping", String.class);
        return new FylkesinndelingMedNavEnheter(objectMapper.readValue(json, new TypeReference<Map<String, List<KommuneEllerBydel>>>() {
        }));
    }

    @SneakyThrows
    public void oppdaterInformasjonFraNorg(
            FylkesinndelingMedNavEnheter fylkesinndeling,
            Map<String, NavEnhet> kommuneNrEllerBydelNrTilNavEnhet
    ) {
        jdbcTemplate.update(
                "UPDATE norg_mapping SET sistOppdatert=?, mapFraFylkesenheterTilKommunerOgBydeler=?, mapFraKommunerOgBydelerTilNavEnheter=?",
                LocalDateTime.now(),
                objectMapper.writeValueAsString(fylkesinndeling.getFylkeTilKommuneEllerBydel()),
                objectMapper.writeValueAsString(kommuneNrEllerBydelNrTilNavEnhet)
        );
    }
}
