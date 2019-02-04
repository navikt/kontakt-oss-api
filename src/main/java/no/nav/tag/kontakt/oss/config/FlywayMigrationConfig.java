package no.nav.tag.kontakt.oss.config;


import no.nav.tag.kontakt.oss.config.DatabaseConfig;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Profile({"preprod", "prod"})
public class FlywayMigrationConfig implements FlywayMigrationStrategy {

    @Autowired
    @Qualifier("adminDataSource")
    private DataSource dataSource;

    @Value("${NAIS_CLUSTER_NAME}")
    private String miljo;

    @Override
    public void migrate(Flyway flyway) {
        flyway.configure()
                .dataSource(dataSource)
                .initSql(String.format("SET ROLE \"%s\"", DatabaseConfig.dbRole(miljo, "admin")))
                .load()
                .migrate();
    }
}