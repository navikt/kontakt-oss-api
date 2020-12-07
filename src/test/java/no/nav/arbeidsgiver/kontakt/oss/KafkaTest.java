package no.nav.arbeidsgiver.kontakt.oss;

import no.nav.arbeidsgiver.kontakt.oss.events.GsakOppgaveOpprettet;
import no.nav.arbeidsgiver.kontakt.oss.kafka.KontaktskjemaMottattProducer;
import no.nav.arbeidsgiver.kontakt.oss.testUtils.TestData;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
@ActiveProfiles({"kafka-test", "local"})
@TestPropertySource(properties = {"mock.enabled=false"})
@EmbeddedKafka(
        controlledShutdown = true,
        partitions = 1,
        topics = {KontaktskjemaMottattProducer.TOPIC}
)
public class KafkaTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<String, String> consumer;

    @BeforeEach
    public void setUp() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        ConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        consumer = cf.createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, KontaktskjemaMottattProducer.TOPIC);
    }

    @Test
    public void besvarelseMottatt__skal_sende_kontaktskjema_på_kafka_topic_med_riktige_felter() throws JSONException {
        Kontaktskjema kontaktskjema = TestData.kontaktskjema();
        kontaktskjema.setId(1);
        Integer gsakId = 2;
        eventPublisher.publishEvent(new GsakOppgaveOpprettet(gsakId, kontaktskjema));

        ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, KontaktskjemaMottattProducer.TOPIC);

        JSONObject json = new JSONObject(record.value());
        assertThat(json.getInt("id")).isEqualTo(kontaktskjema.getId());
        assertThat(json.getInt("gsakId")).isEqualTo(gsakId);

        LocalDateTime opprettet = LocalDateTime.parse(json.getString("opprettet"));
        assertThat(opprettet).isEqualToIgnoringNanos(kontaktskjema.getOpprettet());

        assertThat(json.getString("fylke")).isEqualTo(kontaktskjema.getFylkesenhetsnr());
        assertThat(json.getString("kommune")).isEqualTo(kontaktskjema.getKommune());
        assertThat(json.getString("kommunenr")).isEqualTo(kontaktskjema.getKommunenr());
        assertThat(json.getString("bedriftsnavn")).isEqualTo(kontaktskjema.getBedriftsnavn());
        assertThat(json.getString("orgnr")).isEqualTo(kontaktskjema.getOrgnr());
        assertThat(json.getString("tema")).isEqualTo(kontaktskjema.getTema());
        assertThat(json.getString("temaType")).isEqualTo(kontaktskjema.getTemaType().toString());
    }
}
