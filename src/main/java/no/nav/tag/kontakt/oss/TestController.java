package no.nav.tag.kontakt.oss;

import io.micrometer.core.instrument.Counter;
import no.nav.tag.kontakt.oss.metrics.MetricsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private final MetricsConfig testService;
    private final Counter counter;

    @Autowired
    public TestController(MetricsConfig testService, Counter counter) {
        this.testService = testService;
        this.counter = counter;
    }

    @GetMapping(value = "${controller.basepath}/test")
    public String test() {
        counter.increment();
        return "test";
    }
}
