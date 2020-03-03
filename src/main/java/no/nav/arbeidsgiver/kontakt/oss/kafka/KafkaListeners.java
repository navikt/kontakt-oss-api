package no.nav.arbeidsgiver.kontakt.oss.kafka;

import no.nav.arbeidsgiver.kontakt.oss.events.GsakOppgaveOpprettet;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Profile({"kafka-test", "dev", "prod"})
@Component
public class KafkaListeners {

    private final KontaktskjemaMottattProducer kontaktskjemaMottattProducer;

    public KafkaListeners(KontaktskjemaMottattProducer kontaktskjemaMottattProducer) {
        this.kontaktskjemaMottattProducer = kontaktskjemaMottattProducer;
    }

    @EventListener
    public void besvarelseMottatt(GsakOppgaveOpprettet event) {
        kontaktskjemaMottattProducer.kontaktskjemaMottatt(event.getKontaktskjema(), event.getGsakId());
    }
}
