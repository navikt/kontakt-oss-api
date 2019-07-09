package no.nav.tag.kontakt.oss.metrics;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.events.BesvarelseMottatt;
import no.nav.tag.kontakt.oss.events.FylkesinndelingOppdatert;
import no.nav.tag.kontakt.oss.events.GsakOppgaveSendt;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MetricsListeners {

    @EventListener
    public void besvarelseMottatt(BesvarelseMottatt event) {
        Kontaktskjema kontaktskjema = event.getKontaktskjema();

        // Setter komma til slutt for å skille mellom verdier som starter likt,
        // f.eks. REKRUTTERING og REKRUTTERING_MED_TILRETTELEGGING.
        log.info(
                "event=kontaktskjema.mottatt"
                + ",success=" + event.isSuksess()
                + ",fylke=" + kontaktskjema.getFylke()
                + ",kommunenr=" + kontaktskjema.getKommunenr()
                + ",kommune=" + kontaktskjema.getKommune()
                + ",orgnr=" + kontaktskjema.getOrgnr()
                + ",temaType=" + kontaktskjema.getTemaType()
                + ",harSnakketMedAnsattrepresentant=" + kontaktskjema.getHarSnakketMedAnsattrepresentant()
                + ","
        );
    }

    @EventListener
    public void gsakOppgaveSendt(GsakOppgaveSendt event) {
        log.info("event=gsakoppgave.sendt, success={},", event.isSuksess());
    }

    @EventListener
    public void fylkesInndelingOppdatert(FylkesinndelingOppdatert event) {
        log.info("event=fylkesinndeling.oppdatert, success={},", event.isSuksess());
    }
}
