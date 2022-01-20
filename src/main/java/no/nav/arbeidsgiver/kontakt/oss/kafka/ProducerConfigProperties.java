package no.nav.arbeidsgiver.kontakt.oss.kafka;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kontakt-oss.kafka")
public class ProducerConfigProperties {
    private String gcpBootstrapServers;
    private String sslTruststoreLocationEnvKey;
    private String sslTruststorePasswordEnvKey;
    private String sslKeystoreLocationEnvKey;
    private String sslKeystorePasswordEnvKey;
}
