package no.nav.arbeidsgiver.kontakt.oss.fylkesinndeling;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface FylkesinndelingDAO {
    List<KommuneEllerBydel> hentFylkesinndelinger() throws JsonProcessingException;
    int oppdatereFylkesinndelinger(List<KommuneEllerBydel> municipalities) throws JsonProcessingException;
}
