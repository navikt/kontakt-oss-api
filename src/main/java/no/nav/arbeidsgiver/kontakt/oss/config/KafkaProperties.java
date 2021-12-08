package no.nav.arbeidsgiver.kontakt.oss.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@ConfigurationProperties(prefix = "kafka.outbound")
@Component
public class KafkaProperties {

    private final String valueSerializerClass = StringSerializer.class.getName();
    private final String keySerializerCLass = StringSerializer.class.getName();
    private final String clientId = "kontakt-oss-api";
    private final String acks = "1";

    private final Integer maxInFlightRequestsPerConnection = 5; // default
    private final Integer batchSize = 16384*10;
    private final Integer deliveryTimeoutMs = 120000; // 2 min (default)
    private final Integer requestTimeoutMs = 10000;
    private final Integer lingerMs = 100;
    private final Integer retries = 10;

    private String credstorePassword;
    private String bootstrapServers;
    private String securityProtocol;
    private String truststorePath;
    private String keystorePath;
    private String caPath;
    private String topic;


    public Map<String, Object> asProperties() {
        HashMap<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxInFlightRequestsPerConnection);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClass);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializerCLass);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeoutMs);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.ACKS_CONFIG, acks);

        if(bootstrapServers != null){
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        }

        if(credstorePassword != null) {
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, credstorePassword);
            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, credstorePassword);
            props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, credstorePassword);
            props.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, "PKCS12");
            props.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "JKS");

        }

        if(securityProtocol != null) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        }

        if(truststorePath != null){
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststorePath);
        }

        if(keystorePath != null){
            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystorePath);
        }

        return props;
    }

}
