package no.nav.arbeidsgiver.kontakt.oss.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@ConditionalOnProperty("kontakt-oss.kafka.enabled")
@Component
@Slf4j
public class KontaktskjemaKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KontaktskjemaKafkaProducer(@Qualifier("producerKafkaTemplate") KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private void publiserMelding() {

    }
}
