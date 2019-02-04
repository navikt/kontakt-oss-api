package no.nav.tag.kontakt.oss;


import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
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
    private static String miljo;

    @Override
    public void migrate(Flyway flyway) {
        flyway.configure()
                .dataSource(dataSource)
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
