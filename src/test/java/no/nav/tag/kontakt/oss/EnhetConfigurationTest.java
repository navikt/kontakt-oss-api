package no.nav.tag.kontakt.oss;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"mock.enabled=false"})
public class EnhetConfigurationTest {
    @Autowired
    private Map<String, String> enheter;

    @Test
    public void skalHaRiktigAntallKommuner() {
        assertEquals(112, enheter.size());
    }

    @Test
    public void kunTreKommunerHarEtAnnetNavKontor() {
        List<String> kommunerMedForskjelligKommunenrOgEnhetsnr = enheter.keySet().stream()
                .filter(kommunenr -> !kommunenr.equals(enheter.get(kommunenr)))
                .collect(Collectors.toList());
        System.out.println(kommunerMedForskjelligKommunenrOgEnhetsnr);
        assertEquals(Arrays.asList("1853", "1852", "0138"), kommunerMedForskjelligKommunenrOgEnhetsnr);
    }
}