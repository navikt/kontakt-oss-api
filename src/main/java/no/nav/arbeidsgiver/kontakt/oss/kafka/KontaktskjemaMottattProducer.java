package no.nav.arbeidsgiver.kontakt.oss.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class KontaktskjemaMottattProducer {

    public static final String TOPIC = "aapen-tag-kontaktskjemaMottatt";
    private KafkaTemplate<String, String> kafkaTemplate;

    public KontaktskjemaMottattProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void kontaktskjemaMottatt(Kontaktskjema kontaktskjema, Integer gsakId) {
        KontaktskjemaForKafka kontaktskjemaForKafka = KontaktskjemaForKafka.kontaktskjemaForKafka(kontaktskjema, gsakId);

        try {
            ObjectMapper objectMapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            String serialisertKontaktskjema = objectMapper.writeValueAsString(kontaktskjemaForKafka);

            kafkaTemplate.send(
                    TOPIC,
                    kontaktskjemaForKafka.getId().toString(),
                    serialisertKontaktskjema).get();

            log.info("Kontaktskjema med id {} sendt på Kafka topic", kontaktskjemaForKafka.getId());

        } catch (JsonProcessingException e) {
            log.error("Kunne ikke serialisere kontaktskjema", e);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Kunne ikke sende kontaktskjema på Kafka topic", e);
        }
    }
}
