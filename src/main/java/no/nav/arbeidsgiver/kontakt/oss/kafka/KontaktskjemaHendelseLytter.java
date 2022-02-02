package no.nav.arbeidsgiver.kontakt.oss.kafka;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty("kontakt-oss.kafka.enabled")
@RequiredArgsConstructor
public class KontaktskjemaHendelseLytter {

    private final KontaktskjemaKafkaProducer kontaktskjemaKafkaProducer;

    @EventListener
    public void opprettKafkaMelding(KontaktskjemaKlarTilsending kontaktskjemaKlarTilsending) {
        kontaktskjemaKafkaProducer.publiserMelding(kontaktskjemaKlarTilsending.getKontaktskjema());
    }
}
