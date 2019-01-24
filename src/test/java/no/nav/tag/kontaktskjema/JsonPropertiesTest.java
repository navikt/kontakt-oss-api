package no.nav.tag.kontaktskjema;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class JsonPropertiesTest {
    @Autowired
    private Map<String, String> enheter;

    @Test
    public void whenPropertiesLoadedViaJsonPropertySource_thenLoadFlatValues() {
        assertNotEquals("", enheter);
    }
}