package no.nav.tag.kontakt.oss.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static no.nav.tag.kontakt.oss.kafka.KontaktskjemaMottattProducer.TOPIC;

@Profile({"preprod"})
@Component
@Slf4j
public class KontaktSkjemaMottattConsumer {

    @KafkaListener(topics = TOPIC)
    public void listen(ConsumerRecord<String, String> consumerRecord) {
        log.info(
                "Mottatt melding med offset {}, id {} og value {}",
                consumerRecord.offset(),
                consumerRecord.key(),
                consumerRecord.value()
        );
    }
}
