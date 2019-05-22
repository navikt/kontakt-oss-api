package no.nav.tag.kontakt.oss.kafka;

import no.nav.tag.kontakt.oss.KontaktskjemaRepository;
import no.nav.tag.kontakt.oss.events.BesvarelseMottatt;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AltPaKafkaController {

    private final KontaktskjemaRepository repository;

    private final ApplicationEventPublisher eventPublisher;

    public AltPaKafkaController(KontaktskjemaRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/internal/altpakafka")
    public ResponseEntity altPaKafka() {
        repository.findAll().forEach(kontaktskjema -> eventPublisher.publishEvent(new BesvarelseMottatt(true, kontaktskjema)));
        return ResponseEntity.ok().build();
    }
}
