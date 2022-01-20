package no.nav.arbeidsgiver.kontakt.oss.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

@ConditionalOnProperty("kontakt-oss.kafka.config.enabled")
@Configuration
@Slf4j
@EnableKafka
public class ProducerConfig {
    private final ProducerConfigProperties configProps;

    public ProducerConfig(ProducerConfigProperties producerConfigProperties) {
        this.configProps = producerConfigProperties;
    }

    private Map<String, Object> producerConfigs() {
        final String javaKeystore = "jks";
        final String pkcs12 = "PKCS12";

        Map<String, Object> props = new HashMap<>();
        props.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, configProps.getBootstrapServers());
        props.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SSL.name);
        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
        props.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, javaKeystore);
        props.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, pkcs12);
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, configProps.getSslTruststoreLocationEnvKey());
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, configProps.getSslTruststorePasswordEnvKey());
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, configProps.getSslKeystoreLocationEnvKey());
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, configProps.getSslKeystorePasswordEnvKey());
        return props;
    }

    @Bean
    public KafkaTemplate<String, String> producerKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerConfigs()));
    }
}
