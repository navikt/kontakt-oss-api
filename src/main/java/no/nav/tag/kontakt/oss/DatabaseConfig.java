package no.nav.tag.kontakt.oss;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import no.nav.tag.kontakt.oss.vault.HikariCPVaultUtil;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

//import no.nav.apiapp.selftest.HelsesjekkMetadata;
//import no.nav.sbl.dialogarena.types.Pingable;
//import static no.nav.fo.veilarblest.config.ApplicationConfig.APPLICATION_NAME;
//import static no.nav.sbl.util.EnvironmentUtils.EnviromentClass.P;
//import static no.nav.sbl.util.EnvironmentUtils.*;



@Configuration
@Profile({"preprod", "prod"})
public class DatabaseConfig {

//    public static final String VEILARBLEST_DB_URL_PROPERTY = "VEILARBLEST_DB_URL";
//    public static final String VEILARBLEST_DB_USER_PROPERTY = "VEILARBLEST_DB_USER";
//    public static final String VEILARBLEST_DB_PASSWORD_PROPERTY = "VEILARBLEST_DB_PASSWORD";

    @Value("${DATABASE_URL}")
    private String databaseUrl;

    @Value("${NAIS_CLUSTER_NAME}")
    private String miljo;

    @Bean("adminDataSource")
    public DataSource adminDataSource() {
        return dataSource("admin");
    }

    @Bean
    @Primary
    public DataSource userDataSource() {
        return dataSource("user");
    }

    @Bean
    public DSLContext dslContext(DataSource userDataSource) {
        return DSL.using(userDataSource, SQLDialect.POSTGRES);
    }

    private void migrateDatabase(DataSource dataSource) {
        Flyway.configure()
                .dataSource(dataSource)
                .initSql(String.format("SET ROLE \"%s\"", dbRole("admin")))
                .load()
                .migrate();
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

    private String dbRole(String role) {
        String databaseNavnProd = "kontakt-oss-prod";
        String databaseNavnPreprod = "kontakt-oss-preprod";
        return miljo.equals("prod-fss")
                ? String.join("-", databaseNavnProd, role)
                : String.join("-", databaseNavnPreprod, role);
    }


//    @Bean
//    public Pingable dbPinger(final DSLContext dslContext) {
//        HelsesjekkMetadata metadata = new HelsesjekkMetadata("db",
//                "Database: " + getRequiredProperty(VEILARBLEST_DB_URL_PROPERTY),
//                "Database for veilarblest",
//                true);
//
//        return () -> {
//            try {
//                dslContext.selectOne().fetch();
//                return Pingable.Ping.lyktes(metadata);
//            } catch (Exception e) {
//                return Pingable.Ping.feilet(metadata, e);
//            }
//        };
//    }


}
