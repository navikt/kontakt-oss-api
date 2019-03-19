package no.nav.tag.kontakt.oss.metrics;

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
        String counterName = success ? "mottatt_kontaktskjema_success" : "mottatt_kontaktskjema_fail";
        meterRegistry.counter(
                counterName,
                "fylke", kontaktskjema.getFylke(),
                "kommune", kontaktskjema.getKommunenr(),
                "tema", kontaktskjema.getTema()
        ).increment();
    }

    public void sendtGsakOppgave(boolean success) {
        if (success) {
            meterRegistry.counter("sendt_gsakoppgave_success").increment();
        } else {
            meterRegistry.counter("sendt_gsakoppgave_fail").increment();
        }
    }
}
