package no.nav.tag.kontakt.oss.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Profile({"preprod"})
@Component
@Slf4j
public class KontaktSkjemaMottattConsumer {

    @KafkaListener(topics = "aapen-tag-kontaktskjemaMottatt")
    public void listen(ConsumerRecord<String, String> consumerRecord) {
        log.info(
                "Mottatt melding med offset {}, id {} og value {}",
                consumerRecord.offset(),
                consumerRecord.key(),
                consumerRecord.value()
        );

        try {
            ObjectMapper mapper = new ObjectMapper();
            //JSON string to Java Object
            KontaktskjemaForKafka skjema = mapper.readValue(consumerRecord.value(), KontaktskjemaForKafka.class);
            log.info("Bedriftsnavn: {}", skjema.getBedriftsnavn());

        } catch (IOException e) {
            log.error("Kunne ikke deserialisere skjema", e);
        }
    }

}
