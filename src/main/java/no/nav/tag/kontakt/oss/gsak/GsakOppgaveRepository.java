package no.nav.tag.kontakt.oss.gsak;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface GsakOppgaveRepository extends CrudRepository<GsakOppgave, Integer> {

    @Query("SELECT gsak_id from GSAK_OPPGAVE where kontaktskjema_id = :id")
    public Integer finnGsakIdMedKontaktskjemaId(@Param(value = "id") Integer id);

}
