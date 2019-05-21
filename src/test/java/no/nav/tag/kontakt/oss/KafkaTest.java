package no.nav.tag.kontakt.oss;

import no.nav.tag.kontakt.oss.events.BesvarelseMottatt;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Map;

import static no.nav.tag.kontakt.oss.TestData.kontaktskjema;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"kafka-test", "dev"})
@TestPropertySource(properties = {"mock.enabled=false"})
@DirtiesContext
public class KafkaTest {

    static final String TOPIC = "aapen-tag-kontaktskjemaMottatt";

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true, 1, TOPIC);

    private Consumer<String, String> consumer;

    @Before
    public void setUp() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafka.getEmbeddedKafka());
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        ConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        consumer = cf.createConsumer();
        embeddedKafka.getEmbeddedKafka().consumeFromAnEmbeddedTopic(consumer, TOPIC);
    }

    @Test
    public void besvarelseMottatt__skal_sende_kontaktskjema_på_kafka_topic_med_riktige_felter() throws JSONException {
        Kontaktskjema kontaktskjema = kontaktskjema();
        kontaktskjema.setId(1);
        eventPublisher.publishEvent(new BesvarelseMottatt(true, kontaktskjema));

        ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, TOPIC);

        JSONObject json = new JSONObject(record.value());
        assertThat(json.getInt("id")).isEqualTo(kontaktskjema.getId());

        LocalDateTime opprettet = LocalDateTime.parse(json.getString("opprettet"));
        assertThat(opprettet).isEqualToIgnoringNanos(kontaktskjema.getOpprettet());

        assertThat(json.getString("fylke")).isEqualTo(kontaktskjema.getFylke());
        assertThat(json.getString("kommune")).isEqualTo(kontaktskjema.getKommune());
        assertThat(json.getString("kommunenr")).isEqualTo(kontaktskjema.getKommunenr());
        assertThat(json.getString("bedriftsnavn")).isEqualTo(kontaktskjema.getBedriftsnavn());
        assertThat(json.getString("orgnr")).isEqualTo(kontaktskjema.getOrgnr());
        assertThat(json.getString("tema")).isEqualTo(kontaktskjema.getTema());
        assertThat(json.getString("temaType")).isEqualTo(kontaktskjema.getTemaType().toString());
    }
}
