package no.nav.arbeidsgiver.kontakt.oss.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@DirtiesContext
@RunWith(SpringRunner.class)
@ActiveProfiles({"kafka-test", "local"})
@EmbeddedKafka(
        controlledShutdown = true,
        topics = {"arbeidsgiver.arbeidsgiver-kontaktskjema"},
        partitions = 1
)
@TestPropertySource(properties = {"mock.enabled=false"})
public class KafkaMessageProducerTest {
    private static String TOPIC_NAME = "arbeidsgiver-kontaktskjema";

    @Autowired
    private KafkaMessageProducer kafkaMessageProducer;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private BlockingQueue<ConsumerRecord<String, String>> consumerRecords;
    private KafkaMessageListenerContainer<String, String> container;

    @Before
    public void setUp() {

        consumerRecords = new LinkedBlockingQueue<>();

        ContainerProperties containerProperties = new ContainerProperties(TOPIC_NAME);
        Map<String, Object> consumerProperties =
                KafkaTestUtils.consumerProps(
                        "consumer",
                        "false",
                        embeddedKafkaBroker
                );
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "60000");
        DefaultKafkaConsumerFactory<String, String> kafkaConsumerFactory =
                new DefaultKafkaConsumerFactory<>(consumerProperties);

        container = new KafkaMessageListenerContainer<>(kafkaConsumerFactory, containerProperties);
        container.setupMessageListener((MessageListener<String, String>) record -> {
            consumerRecords.add(record);
        });

    }

    @After
    public void tearDown() {
        container.stop();
    }

    @Test
    public void messageRecieved() throws InterruptedException, IOException {
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());

        FormSubmission submission = FormSubmission.builder().Id(1234).email("Test@nav.no").build();
        String serializedSubmission = new ObjectMapper().writeValueAsString(submission);

        kafkaMessageProducer.publish("1234", serializedSubmission);

        ConsumerRecord<String, String> received = consumerRecords.poll(10, TimeUnit.SECONDS);

        Assert.assertEquals(serializedSubmission, received.value());

    }

}
