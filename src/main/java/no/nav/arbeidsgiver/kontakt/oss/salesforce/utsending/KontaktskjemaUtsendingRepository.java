package no.nav.arbeidsgiver.kontakt.oss.salesforce.utsending;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface KontaktskjemaUtsendingRepository extends CrudRepository<KontaktskjemaUtsending, Integer> {

    @Query("select ku.* from KONTAKTSKJEMA_UTSENDING ku where ku.kontaktskjema_id=:kontaktskjemaId and ku.utsending_status <> 'SENT'")
    KontaktskjemaUtsending hentKontakskjemaUtsending(@Param("kontaktskjemaId") Integer id);

}
