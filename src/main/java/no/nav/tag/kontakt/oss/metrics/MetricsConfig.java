package no.nav.tag.kontakt.oss.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MetricsConfig {

    private final MeterRegistry meterRegistry;
    private final Counter testCounter;

    @Autowired
    public MetricsConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.testCounter = meterRegistry.counter("test.test", "hei1", "yo1");
    }

    @Bean
    public Counter testCounter() {
        return testCounter;
    }
}
