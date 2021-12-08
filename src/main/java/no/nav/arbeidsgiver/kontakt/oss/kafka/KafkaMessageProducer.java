package no.nav.arbeidsgiver.kontakt.oss.kafka;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class KafkaMessageProducer {
    private static final String TOPIC = "arbeidsgiver-kontaktskjema";
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaMessageProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @SneakyThrows
    public void publish(String key, String value) {

        this.kafkaTemplate.send(
                TOPIC,
                key,
                value
        ).get(10, TimeUnit.SECONDS);

    }

}
