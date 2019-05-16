package no.nav.tag.kontakt.oss.config;

import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.kafka.KontaktskjemaForKafka;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public KafkaTemplate<String, KontaktskjemaForKafka> kafkaTemplate(ProducerFactory<String, KontaktskjemaForKafka> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    // TODO: Denne skal bare være for preprod/prod. Skal ha en egen embedded kafka config for test
//    @Profile({"preprod", "prod"})
    @Bean
    public ProducerFactory<String, KontaktskjemaForKafka> producerFactory(KafkaProperties properties) {
        return new DefaultKafkaProducerFactory<>(properties.buildProducerProperties());

        // TODO: Legg til mapper for Kontaktskjema
//        ObjectMapper objectMapper = new ObjectMapper()
//                .registerModule(new JavaTimeModule())
//                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        return new DefaultKafkaProducerFactory<>(
//                properties.buildProducerProperties(),
//                new StringSerializer(),
//                new FunctionSerializer<>(value -> {
//                    try {
//                        return objectMapper.writeValueAsBytes(value);
//                    } catch (JsonProcessingException jsonProcessingException) {
//                        throw new RuntimeException(jsonProcessingException);
//                    }
//                }));
    }

}
