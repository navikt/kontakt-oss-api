package no.nav.tag.kontakt.oss;

import no.nav.tag.kontakt.oss.events.BesvarelseMottatt;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.KafkaUtils;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import scala.collection.immutable.Stream;


import java.util.Map;

import static no.nav.tag.kontakt.oss.TestData.kontaktskjema;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"kafka", "dev"})
@TestPropertySource(properties = {"mock.enabled=false"})
@DirtiesContext
public class KafkaTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public static final String TOPIC = "aapen-tag-kontaktskjemaMottatt";

    // TOOO: trenger vi true her for graceful shutdown?
    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true, 1, TOPIC);

    @Test
    public void besvarelseMottatt__skal_sende_kontaktskjema_på_kafka_topic() {
        Kontaktskjema kontaktskjema = kontaktskjema();
        kontaktskjema.setId(1);

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafka.getEmbeddedKafka());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        eventPublisher.publishEvent(new BesvarelseMottatt(true, kontaktskjema));
        ConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        Consumer<String, String> consumer = cf.createConsumer();

        embeddedKafka.getEmbeddedKafka().consumeFromAnEmbeddedTopic(consumer, TOPIC);

        ConsumerRecords<String, String> replies = KafkaTestUtils.getRecords(consumer);

        System.out.println(replies);

        assertThat(replies.count()).isGreaterThanOrEqualTo(1);

        // Sjekk at konsumert string inneholder riktige verdier. ex. bedriftsnr ++
    }
}
