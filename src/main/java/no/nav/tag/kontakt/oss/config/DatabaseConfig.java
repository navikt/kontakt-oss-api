package no.nav.tag.kontakt.oss.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile({"preprod", "prod"})
public class DatabaseConfig {
    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy(
            @Autowired DataSource dataSource,
            @Value("${spring.cloud.vault.database.role}") String adminRolle
    ) {
        return flyway -> Flyway.configure()
                .dataSource(dataSource)
                .initSql(String.format("SET ROLE \"%s\"", adminRolle))
                .load()
                .migrate();
    }
}
