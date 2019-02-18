package no.nav.tag.kontakt.oss.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MetricsConfig {

    private final Metrics metrics;

    @Autowired
    public MetricsConfig(MeterRegistry meterRegistry) {
        this.metrics = new Metrics(
                meterRegistry.counter("mottatt_kontaktskjema_success"),
                meterRegistry.counter("mottatt_kontaktskjema_fail"),
                meterRegistry.counter("sendt_gsakoppgave_success"),
                meterRegistry.counter("sendt_gsakoppgave_fail")
        );
    }

    @Bean
    public Metrics metrics() {
        return this.metrics;
    }
}
