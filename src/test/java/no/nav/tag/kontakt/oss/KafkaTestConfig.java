package no.nav.tag.kontakt.oss;

import org.junit.ClassRule;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;

//@TestConfiguration
//@Configuration
//@EnableKafka
public class KafkaTestConfig {

    // TODO: Få til å kjøre en embedded kafka
    @ClassRule
    public static EmbeddedKafkaBroker embeddedKafkaBroker = new EmbeddedKafkaBroker(1, true, "aapen-tag-kontaktskjemaMottatt-q");

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
    }
}
