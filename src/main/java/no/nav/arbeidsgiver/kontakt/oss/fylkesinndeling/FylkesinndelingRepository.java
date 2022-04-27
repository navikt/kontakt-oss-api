package no.nav.arbeidsgiver.kontakt.oss.fylkesinndeling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


@Repository
public class FylkesinndelingRepository implements FylkesinndelingDAO {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final JdbcTemplate jdbcTemplate;


    public FylkesinndelingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<KommuneEllerBydel> hentFylkesinndelinger() throws JsonProcessingException {
        String sql = "SELECT kommune_bydel FROM FYLKESINNDELING";
        String json = jdbcTemplate.queryForObject(sql, String.class);

        return Arrays.asList(objectMapper.readValue(json, KommuneEllerBydel[].class));
    }

    @Override
    public int oppdatereFylkesinndelinger(List<KommuneEllerBydel> municipalities) throws JsonProcessingException {
        String json =  objectMapper.writeValueAsString(municipalities);
        String sql = "UPDATE FYLKESINNDELING SET kommune_bydel=?, last_updated=?";

        return jdbcTemplate.update(sql, json, LocalDateTime.now());
    }
}
