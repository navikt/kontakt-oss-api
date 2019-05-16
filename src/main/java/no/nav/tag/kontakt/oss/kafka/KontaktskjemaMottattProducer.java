package no.nav.tag.kontakt.oss.kafka;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static java.util.UUID.randomUUID;
import static no.nav.tag.kontakt.oss.kafka.KontaktskjemaForKafka.kontaktskjemaForKafka;

@Component
@Slf4j
public class KontaktskjemaMottattProducer {

    private KafkaTemplate<String, KontaktskjemaForKafka> kafkaTemplate;

    public KontaktskjemaMottattProducer(KafkaTemplate<String, KontaktskjemaForKafka> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void kontaktskjemaMottatt(Kontaktskjema kontaktskjema) {
        KontaktskjemaForKafka kontaktskjemaForKafka = kontaktskjemaForKafka(kontaktskjema);

        kafkaTemplate.send(
                "aapen-tag-kontaktskjemaMottatt",
                randomUUID().toString(),
                kontaktskjemaForKafka);

        log.info("Kontaktskjema med id: {}, er sendt på Kafka topic", kontaktskjemaForKafka.getId());
    }
}
