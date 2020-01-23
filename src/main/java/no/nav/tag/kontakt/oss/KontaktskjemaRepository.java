package no.nav.tag.kontakt.oss;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface KontaktskjemaRepository extends CrudRepository<Kontaktskjema, Integer> {

    @Query("SELECT k.* FROM Kontaktskjema k WHERE k.opprettet > :created")
    public Collection<Kontaktskjema> findAllNewerThan(@Param(value = "created") LocalDateTime created);

    @Query("SELECT k.* FROM Kontaktskjema k WHERE NOT EXISTS (SELECT g.id FROM GSAK_OPPGAVE g WHERE k.id = g.kontaktskjema_id AND g.status <> 'FEILET')")
    public Collection<Kontaktskjema> findAllWithNoGsakOppgave();

    @Query("SELECT k.* FROM Kontaktskjema k WHERE EXISTS (SELECT ku.id FROM KONTAKTSKJEMA_UTSENDING ku WHERE k.id = ku.kontaktskjema_id AND ku.utsending_status <> 'SENT')")
    public Collection<Kontaktskjema> hentKontakskjemaerSomSkalSendesTilSalesforce();

    @Query("SELECT 'ok'")
    public String healthcheck();
}
