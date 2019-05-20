package no.nav.tag.kontakt.oss.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static java.util.UUID.randomUUID;
import static no.nav.tag.kontakt.oss.kafka.KontaktskjemaForKafka.kontaktskjemaForKafka;

@Component
@Slf4j
public class KontaktskjemaMottattProducer {

    private KafkaTemplate<String, String> kafkaTemplate;

    public KontaktskjemaMottattProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void kontaktskjemaMottatt(Kontaktskjema kontaktskjema) {
        KontaktskjemaForKafka kontaktskjemaForKafka = kontaktskjemaForKafka(kontaktskjema);

        try {
            String serialisertKontaktskjema = new ObjectMapper().writeValueAsString(kontaktskjemaForKafka);

            kafkaTemplate.send(
                    "aapen-tag-kontaktskjemaMottatt",
                    randomUUID().toString(),
                    serialisertKontaktskjema);

            log.info("Kontaktskjema med id: {}, er sendt på Kafka topic", kontaktskjemaForKafka.getId());

        } catch (JsonProcessingException e) {
            log.error("Kunne ikke serialisere kontaktskjema", e);
        }
    }
}
