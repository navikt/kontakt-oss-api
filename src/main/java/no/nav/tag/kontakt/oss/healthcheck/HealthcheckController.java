package no.nav.tag.kontakt.oss.healthcheck;

import no.nav.tag.kontakt.oss.KontaktskjemaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthcheckController {
    private final KontaktskjemaRepository repository;

    public HealthcheckController(KontaktskjemaRepository repository) {
        this.repository = repository;
    }

    @GetMapping(value = "/internal/healthcheck")
    public String healthcheck() {
        return repository.healthcheck();
    }
}
