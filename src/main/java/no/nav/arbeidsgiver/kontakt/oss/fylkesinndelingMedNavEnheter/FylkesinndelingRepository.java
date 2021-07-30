package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class FylkesinndelingRepository {
    private static final TypeReference<Map<String, List<KommuneEllerBydel>>> FYLKESINNDELING_TYPE = new TypeReference<>() {
    };

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

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
            Map<String, List<KommuneEllerBydel>> fylkesenhetsnrTilKommunerOgBydeler
    ) {
        jdbcTemplate.update(
                "UPDATE norg_mapping SET sistOppdatert=?, mapFraFylkesenheterTilKommunerOgBydeler=?",
                LocalDateTime.now(),
                objectMapper.writeValueAsString(fylkesenhetsnrTilKommunerOgBydeler)
        );
    }

    @SneakyThrows
    @Deprecated
    public Map<String, List<KommuneEllerBydel>> hentFylkesinndeling() {
        String json = jdbcTemplate.queryForObject("SELECT mapFraFylkesenheterTilKommunerOgBydeler FROM norg_mapping", String.class);
        return objectMapper.readValue(json, FYLKESINNDELING_TYPE);
    }
}
