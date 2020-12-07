package no.nav.arbeidsgiver.kontakt.oss;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"mock.enabled=false"})
public class KontaktskjemaApplicationTests {

    @Test
    public void contextLoads() {
    }

}
