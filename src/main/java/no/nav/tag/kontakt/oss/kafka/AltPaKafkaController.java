package no.nav.tag.kontakt.oss.kafka;

import no.nav.tag.kontakt.oss.KontaktskjemaRepository;
import no.nav.tag.kontakt.oss.events.GsakOppgaveOpprettet;
import no.nav.tag.kontakt.oss.gsak.GsakOppgave;
import no.nav.tag.kontakt.oss.gsak.GsakOppgaveRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AltPaKafkaController {

    private final KontaktskjemaRepository repository;

    private final GsakOppgaveRepository gsakRepository;

    private final ApplicationEventPublisher eventPublisher;

    public AltPaKafkaController(KontaktskjemaRepository repository, GsakOppgaveRepository gsakRepository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.gsakRepository = gsakRepository;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/internal/altpakafka")
    public ResponseEntity altPaKafka() {
        repository.findAll().forEach(kontaktskjema -> {
            GsakOppgave oppgave = gsakRepository.finnGsakIdMedKontaktskjemaId(kontaktskjema.getId());
            eventPublisher.publishEvent(new GsakOppgaveOpprettet(oppgave.getGsakId(), kontaktskjema));
        });
        return ResponseEntity.ok().build();
    }
}
