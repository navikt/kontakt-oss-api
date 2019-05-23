package no.nav.tag.kontakt.oss.gsak;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface GsakOppgaveRepository extends CrudRepository<GsakOppgave, Integer> {

    @Query("SELECT g.* from GSAK_OPPGAVE g where g.kontaktskjema_id = :id AND g.status = 'OK'")
    public GsakOppgave finnGsakIdMedKontaktskjemaId(@Param(value = "id") Integer id);

}
