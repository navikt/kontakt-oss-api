package no.nav.tag.kontakt.oss;

import no.nav.tag.kontakt.oss.events.BesvarelseMottatt;
import no.nav.tag.kontakt.oss.events.FylkesinndelingOppdatert;
import no.nav.tag.kontakt.oss.events.GsakOppgaveSendt;
import no.nav.tag.kontakt.oss.metrics.MetricsListeners;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import static no.nav.tag.kontakt.oss.TestData.kontaktskjema;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MetricsListenersTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @MockBean
    private MetricsListeners metrics;

    @Test
    public void besvarelseMottattSkalKalles() {
        eventPublisher.publishEvent(new BesvarelseMottatt(true, kontaktskjema()));
        verify(metrics).besvarelseMottatt(any());
    }

    @Test
    public void gsakOppgaveSendtSkalKalles() {
        eventPublisher.publishEvent(new GsakOppgaveSendt(true));
        verify(metrics).gsakOppgaveSendt(any());
    }

    @Test
    public void fylkesinndelingOppdatertSkalKalles() {
        eventPublisher.publishEvent(new FylkesinndelingOppdatert(true));
        verify(metrics).fylkesInndelingOppdatert(any());
    }
}
