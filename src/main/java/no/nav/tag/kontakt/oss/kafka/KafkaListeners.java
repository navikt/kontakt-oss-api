package no.nav.tag.kontakt.oss.kafka;

import no.nav.tag.kontakt.oss.events.BesvarelseMottatt;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Profile({"preprod", "prod"})
@Component
public class KafkaListeners {

    private final KontaktskjemaMottattProducer kontaktskjemaMottattProducer;

    public KafkaListeners(KontaktskjemaMottattProducer kontaktskjemaMottattProducer) {
        this.kontaktskjemaMottattProducer = kontaktskjemaMottattProducer;
    }

    @EventListener
    public void besvarelseMottatt(BesvarelseMottatt event) {
        kontaktskjemaMottattProducer.kontaktskjemaMottatt(event.getKontaktskjema());
    }
}
