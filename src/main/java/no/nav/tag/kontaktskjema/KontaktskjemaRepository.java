package no.nav.tag.kontaktskjema;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface KontaktskjemaRepository extends CrudRepository<Kontaktskjema, Integer> {
    
    @Query("SELECT k.* FROM Kontaktskjema k WHERE k.opprettet > :created")
    public Collection<Kontaktskjema> findAllNewerThan(@Param(value = "created") LocalDateTime created);
    
    @Query("SELECT k.* FROM Kontaktskjema k WHERE NOT EXISTS (SELECT g.id FROM GSAK_OPPGAVE g WHERE k.id = g.kontaktskjema_id)")
    public Collection<Kontaktskjema> findAllWithNoGsakOppgave();
    
}
