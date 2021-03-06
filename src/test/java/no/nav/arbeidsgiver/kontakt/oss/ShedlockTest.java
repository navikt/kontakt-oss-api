package no.nav.arbeidsgiver.kontakt.oss;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.function.Predicate.isEqual;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ShedlockTest {


    private JdbcTemplate jdbcTemplate;
    private LockingTaskExecutor taskExecutor;

    @BeforeEach
    public void setUp() {
        DataSource ds = createDataSource();
        jdbcTemplate = new JdbcTemplate(ds);
        taskExecutor = new DefaultLockingTaskExecutor(new JdbcTemplateLockProvider(ds));
    }

    private DataSource createDataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl("jdbc:h2:mem:shedlocktest;DB_CLOSE_DELAY=-1");
        ds.setUsername("sa");
        ds.setPassword("");
        Flyway
                .configure()
                .dataSource(ds)
                .load()
                .migrate();
        return ds;
    }

    @Test
    public void skalSjekkeAtShedlockTabellHarRadForDefinertLaas() throws InterruptedException {
        Instant lockTime = Instant.now().plusSeconds(10);
        taskExecutor.executeWithLock((Runnable) () -> {
        }, new LockConfiguration("shedlockTest1", lockTime, lockTime));
        List<Map<String, Object>> results = jdbcTemplate.queryForList("SELECT * FROM SHEDLOCK");
        int shedlockTestLocks = results.stream().map(m -> (String) m.get("name")).filter(isEqual("shedlockTest1")).collect(toList()).size();
        assertThat(shedlockTestLocks).isEqualTo(1);
    }

    @Test
    public void skalKjoreJobbMedLaasKunEnGang() throws InterruptedException {

        Instant lockTime = Instant.now().plusSeconds(10);
        LockConfiguration lockConfig = new LockConfiguration("shedlockTest2", lockTime, lockTime);

        List<Integer> listSomPopuleresIJobb = new ArrayList<Integer>();
        Stream.iterate(0, i -> i++).limit(10)
                .forEach(t -> {
                    taskExecutor.executeWithLock((Runnable) () -> {
                        log.info("legger til {}", t);
                        listSomPopuleresIJobb.add(t);
                    }, lockConfig);
                });

        assertThat(listSomPopuleresIJobb.size()).isEqualTo(1);
    }

}
