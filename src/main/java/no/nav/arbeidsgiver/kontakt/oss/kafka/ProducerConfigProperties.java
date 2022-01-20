package no.nav.arbeidsgiver.kontakt.oss.kafka;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@ConditionalOnProperty("kontakt-oss.kafka.config.enabled")
@Data
@Component
public class ProducerConfigProperties {
    @Value("${kontakt-oss.kafka.config.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kontakt-oss.kafka.config.truststore-path}")
    private String sslTruststoreLocationEnvKey;

    @Value("${kontakt-oss.kafka.config.truststore-password}")
    private String sslTruststorePasswordEnvKey;

    @Value("${kontakt-oss.kafka.config.keystore-path}")
    private String sslKeystoreLocationEnvKey;

    @Value("${kontakt-oss.kafka.config.keystore-password}")
    private String sslKeystorePasswordEnvKey;
}
