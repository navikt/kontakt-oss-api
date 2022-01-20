package no.nav.arbeidsgiver.kontakt.oss.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.concurrent.ListenableFutureCallback;

@ConditionalOnProperty("kontakt-oss.kafka.enabled")
@Component
@Slf4j
public class KontaktskjemaKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KontaktskjemaKafkaProducer(
            @Qualifier("producerKafkaTemplate") KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    private String parseKontaktskjemaMelding(KontaktskjemaMelding kontaktskjemaMelding, String id, String topic) {
        try {
            return objectMapper.writeValueAsString(kontaktskjemaMelding);
        } catch (JsonProcessingException e) {
            log.error("feilet med å lage JSON for kafka-melding med id {} til topic {}", id, topic);
            return null;
        }
    }

    @TransactionalEventListener
    public void publiserMelding(Kontaktskjema kontaktskjema) {
        String meldingId = kontaktskjema.getId().toString();
        final KontaktskjemaMelding kontaktskjemaMelding = KontaktskjemaMelding.lagKontaktskjemaMelding(kontaktskjema);
        String stringifyMelding = parseKontaktskjemaMelding(kontaktskjemaMelding, meldingId, Topics.KONTAKTSKJEMA);

        if (stringifyMelding != null) {
            kafkaTemplate.send(Topics.KONTAKTSKJEMA, meldingId, stringifyMelding)
                    .addCallback(new ListenableFutureCallback<>() {
                        @Override
                        public void onSuccess(SendResult<String, String> result) {
                            log.info("Melding med id {} sendt til Kafka topic {}", meldingId, Topics.KONTAKTSKJEMA);
                        }

                        @Override
                        public void onFailure(Throwable ex) {
                            log.error("Melding med id {} kunne ikke sendes til Kafka topic {}", meldingId, Topics.KONTAKTSKJEMA);
                        }
                    });
        }
    }
}
