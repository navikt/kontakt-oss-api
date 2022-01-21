package no.nav.arbeidsgiver.kontakt.oss.kafka;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@ConditionalOnProperty("kontakt-oss.kafka.test-instance.enabled")
@Slf4j
@Data
@Component
public class KontaktskjemaKafkaLytter {

    private CountDownLatch latch = new CountDownLatch(1);
    private String payload = null;

    @KafkaListener(topics = Topics.KONTAKTSKJEMA, groupId = "kontakt-oss", containerFactory = "kafkaListenerContainerFactory")
    public void mottattMelding(ConsumerRecord<?, ?> melding) {
        log.info("mottatt melding {}", melding.toString());
        setPayload(melding.toString());
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public String getPayload() {
        return payload;
    }
}
