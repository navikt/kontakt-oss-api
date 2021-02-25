package no.nav.arbeidsgiver.kontakt.oss.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;
import no.nav.arbeidsgiver.kontakt.oss.events.BesvarelseMottatt;
import no.nav.arbeidsgiver.kontakt.oss.events.FylkesinndelingOppdatert;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MetricsListeners {

    private final static String KONTAKTSKJEMA_FEILET_COUNTER = "kontaktskjema.feilet";

    private final MeterRegistry meterRegistry;

    public MetricsListeners(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        meterRegistry.counter(KONTAKTSKJEMA_FEILET_COUNTER);
    }

    @EventListener
    public void besvarelseMottatt(BesvarelseMottatt event) {
        Kontaktskjema kontaktskjema = event.getKontaktskjema();

        if (!event.isSuksess()) {
            meterRegistry.counter(KONTAKTSKJEMA_FEILET_COUNTER).increment();
        }

        // Setter komma til slutt for å skille mellom verdier som starter likt,
        // f.eks. REKRUTTERING og REKRUTTERING_MED_TILRETTELEGGING.
        log.info("event=kontaktskjema.mottatt"
                + ",success=" + event.isSuksess()
                + ",fylke=" + kontaktskjema.getFylkesenhetsnr()
                + ",kommunenr=" + kontaktskjema.getKommunenr()
                + ",kommune=" + kontaktskjema.getKommune()
                + ",orgnr=" + kontaktskjema.getOrgnr()
                + ",temaType=" + kontaktskjema.getTemaType()
                + ",harSnakketMedAnsattrepresentant=" + kontaktskjema.getHarSnakketMedAnsattrepresentant()
                + ","
        );
    }

    @EventListener
    public void fylkesInndelingOppdatert(FylkesinndelingOppdatert event) {
        log.info("event=fylkesinndeling.oppdatert, success={},", event.isSuksess());
    }
}
