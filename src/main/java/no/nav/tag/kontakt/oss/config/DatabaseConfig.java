package no.nav.tag.kontakt.oss.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import no.nav.tag.kontakt.oss.vault.HikariCPVaultUtil;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;


@Configuration
@Profile({"preprod", "prod"})
public class DatabaseConfig {

    @Value("${DATABASE_URL}")
    private String databaseUrl;

    @Value("${NAIS_CLUSTER_NAME}")
    private String miljo;

    @Bean
    public DataSource userDataSource() {
        return dataSource("user");
    }

    @SneakyThrows
    private HikariDataSource dataSource(String user) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(databaseUrl);
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);
        String mountPath = miljo.equals("prod-fss")
                ? "postgresql/prod-fss"
                : "postgresql/preprod-fss";
        return HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration(config, mountPath, dbRole(user));
    }

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> Flyway.configure().dataSource(dataSource("admin"))
                .initSql(String.format("SET ROLE \"%s\"", dbRole("admin")))
                .load()
                .migrate();
    }

    private String dbRole(String role) {
        String databaseNavnProd = "kontakt-oss-prod";
        String databaseNavnPreprod = "kontakt-oss-preprod";
        return miljo.equals("prod-fss")
                ? String.join("-", databaseNavnProd, role)
                : String.join("-", databaseNavnPreprod, role);
    }
}
