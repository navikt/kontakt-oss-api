package no.nav.arbeidsgiver.kontakt.oss;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;

public interface KontaktskjemaRepository extends CrudRepository<Kontaktskjema, Integer> {

    @Query("SELECT k.* FROM Kontaktskjema k WHERE k.opprettet > :created")
    Collection<Kontaktskjema> findAllNewerThan(@Param(value = "created") LocalDateTime created);

    @Query("SELECT k.* FROM Kontaktskjema k WHERE EXISTS (SELECT ku.id FROM KONTAKTSKJEMA_UTSENDING ku WHERE k.id = ku.kontaktskjema_id AND ku.utsending_status <> 'SENT')")
    Collection<Kontaktskjema> hentKontakskjemaerSomSkalSendesTilKafka();

    @Query("SELECT 'ok'")
    String healthcheck();
}
