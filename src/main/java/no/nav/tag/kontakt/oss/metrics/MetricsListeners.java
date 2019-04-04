package no.nav.tag.kontakt.oss.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import no.nav.tag.kontakt.oss.events.BesvarelseMottatt;
import no.nav.tag.kontakt.oss.events.FylkesinndelingOppdatert;
import no.nav.tag.kontakt.oss.events.GsakOppgaveSendt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MetricsListeners {

    private final MeterRegistry meterRegistry;

    @Autowired
    public MetricsListeners(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @EventListener
    public void besvarelseMottatt(BesvarelseMottatt event) {
        String counterName = event.isSuksess() ? "mottatt.kontaktskjema.success" : "mottatt.kontaktskjema.fail";

        Counter.builder(counterName)
                .tag("fylke", event.getKontaktskjema().getFylke())
                .tag("kommune", event.getKontaktskjema().getKommunenr())
                .tag("tema", event.getKontaktskjema().getTema())
                .register(meterRegistry)
                .increment();
    }

    @EventListener
    public void gsakOppgaveSendt(GsakOppgaveSendt event) {
        String counterName = event.isSuksess() ? "sendt.gsakoppgave.success" : "sendt.gsakoppgave.fail";

        Counter.builder(counterName)
                .register(meterRegistry)
                .increment();
    }

    @EventListener
    public void fylkesInndelingOppdatert(FylkesinndelingOppdatert event) {
        String counterName = event.isSuksess() ? "hentet.fylkesinndeling.success" : "hentet.fylkesinndeling.fail";

        Counter.builder(counterName)
                .register(meterRegistry)
                .increment();
    }
}
