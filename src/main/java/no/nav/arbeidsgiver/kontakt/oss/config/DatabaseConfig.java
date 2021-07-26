package no.nav.arbeidsgiver.kontakt.oss.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.vault.jdbc.hikaricp.HikariCPVaultUtil;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile({"dev", "prod"})
@Slf4j
public class DatabaseConfig {

    @Value("${spring.datasource.url:}")
    private String databaseUrl;

    @Value("${database.navn:}")
    private String databaseNavn;

    @Value("${vault.mount-path:}")
    private String mountPath;

    @Value("${db.host:}")
    private String dbHost;

    @Value("${db.port:}")
    private String dbPort;

    @Value("${db.database:}")
    private String dbDatabase;

    @Value("${db.username:}")
    private String dbUsername;

    @Value("${db.password:}")
    private String dbPassword;

    @Value("${nais.cluster.name:}")
    private String naisClusterName;

    @Bean
    public DataSource userDataSource() {
        log.info("nais cluster name: {}", naisClusterName);
        if (naisClusterName.endsWith("gcp")) {
            return gcpDataSource();
        }
        return dataSource("user");
    }

    @SneakyThrows
    private HikariDataSource gcpDataSource() {
        HikariConfig config = new HikariConfig();
        var url = String.format("jdbc:postgresql://%s:%s/%s", dbHost, dbPort, dbDatabase);
        log.info("db connection: url: {} username: {}", url, dbUsername);
        config.setJdbcUrl(url);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setMaximumPoolSize(2);
        config.setMinimumIdle(1);
        return new HikariDataSource(config);
    }

    @SneakyThrows
    private HikariDataSource dataSource(String user) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(databaseUrl);
        config.setMaximumPoolSize(2);
        config.setMinimumIdle(1);
        return HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration(config, mountPath, dbRole(user));
    }

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        if (naisClusterName.endsWith("gcp")) {
            return flyway -> Flyway.configure()
                    .dataSource(gcpDataSource())
                    .load()
                    .migrate();
        }
        return flyway -> Flyway.configure()
                .dataSource(dataSource("admin"))
                .initSql(String.format("SET ROLE \"%s\"", dbRole("admin")))
                .load()
                .migrate();
    }

    private String dbRole(String role) {
        return String.join("-", databaseNavn, role);
    }
}
