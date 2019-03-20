package no.nav.tag.kontakt.oss.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import org.springframework.stereotype.Component;

@Component
public class Metrics {
    private final MeterRegistry meterRegistry;

    public Metrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void mottattKontaktskjema(boolean success, Kontaktskjema kontaktskjema) {
        String counterName = success ? "mottatt.kontaktskjema.success" : "mottatt.kontaktskjema.fail";

        Counter.builder(counterName)
                .tag("fylke", kontaktskjema.getFylke())
                .tag("kommune", kontaktskjema.getKommunenr())
                .tag("tema", kontaktskjema.getTema())
                .register(meterRegistry)
                .increment();
    }

    public void sendtGsakOppgave(boolean success) {
        if (success) {
            meterRegistry.counter("sendt_gsakoppgave_success").increment();
        } else {
            meterRegistry.counter("sendt_gsakoppgave_fail").increment();
        }
    }
}
