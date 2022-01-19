package no.nav.arbeidsgiver.kontakt.oss.kafka;


import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;


@ActiveProfiles("local")
@EnableKafka
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {Topics.KONTAKTSKJEMA})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class KontaktskjemaKafkaLytterTest {

    @Autowired
    private EmbeddedKafkaBroker kafkaEmbedded;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;


    @BeforeAll
    public void setUp() throws Exception {
        for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer,
                    kafkaEmbedded.getPartitionsPerTopic());
        }
    }

    @Test
    public void send_melding_pa_kafkatopic() {

        kafkaTemplate.send(Topics.KONTAKTSKJEMA, "sending message with kafkaProducer");
        assertTrue(true);
    }

}
