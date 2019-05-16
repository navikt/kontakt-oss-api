package no.nav.tag.kontakt.oss.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class KontaktskjemaMottattProducer {

    private KafkaTemplate<String, String> kafkaTemplate;

    public KontaktskjemaMottattProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void kontaktskjemaMottatt(Kontaktskjema kontaktskjema) {
        try {
            String serialisertKontaktskjema = new ObjectMapper().writeValueAsString(kontaktskjema);
            kafkaTemplate.send(
                    "aapen-tag-kontaktskjemaMottatt-q",
                    UUID.randomUUID().toString(),
                    serialisertKontaktskjema);

            log.info("Kontaktskjema med id: {}, er sendt på Kafka topic", kontaktskjema.getId());

        } catch (JsonProcessingException e) {
            log.error("Kunne ikke serialisere kontaktskjema", e);
        }
    }
}
