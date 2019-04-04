package no.nav.tag.kontakt.oss;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.tag.kontakt.oss.events.BesvarelseMottatt;
import no.nav.tag.kontakt.oss.events.FylkesinndelingOppdatert;
import no.nav.tag.kontakt.oss.events.GsakOppgaveSendt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import static no.nav.tag.kontakt.oss.TestData.kontaktskjema;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MetricsTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    public void besvarelseMottatt__skal_inkrementere_counter() {
        eventPublisher.publishEvent(new BesvarelseMottatt(true, kontaktskjema()));
        assertThat(hentCount("mottatt.kontaktskjema.success")).isEqualTo(1.0);
    }

    @Test
    public void gsakOppgaveSendt__skal_inkrementere_counter() {
        eventPublisher.publishEvent(new GsakOppgaveSendt(true));
        assertThat(hentCount("sendt.gsakoppgave.success")).isEqualTo(1.0);
    }

    @Test
    public void fylkesInndelingOppdatert__skal_inkrementere_counter() {
        eventPublisher.publishEvent(new FylkesinndelingOppdatert(true));
        assertThat(hentCount("hentet.fylkesinndeling.success")).isEqualTo(1.0);
    }

    private double hentCount(String counterName) {
        return meterRegistry.get(counterName).counter().count();
    }
}
