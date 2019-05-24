package no.nav.tag.kontakt.oss;

import no.finn.unleash.DefaultUnleash;
import no.nav.tag.kontakt.oss.events.BesvarelseMottatt;
import org.junit.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.generate;
import static no.nav.tag.kontakt.oss.TestData.kontaktskjema;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

public class KontaktskjemaControllerTest {

    private int maksInnsendingerPerTiMin = 10;
    private KontaktskjemaRepository repository = mock(KontaktskjemaRepository.class);
    private ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private DefaultUnleash unleash = mock(DefaultUnleash.class);
    private KontaktskjemaController kontaktskjemaController = new KontaktskjemaController(
            repository,
            maksInnsendingerPerTiMin,
            eventPublisher,
            unleash);

    @Test
    public void skalLagreKontaktskjemaOk() {
        kontaktskjemaController.meldInteresse(kontaktskjema());
    }

    @Test
    public void skalReturnereStatus500DersomLagringFeiler() {
        when(repository.save(any(Kontaktskjema.class))).thenThrow(new RuntimeException("Feil ved lagring"));
        assertThat(kontaktskjemaController.meldInteresse(kontaktskjema()).getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void skalReturnere429VedForMangeInnsendinger() {
        when(repository.findAllNewerThan(any(LocalDateTime.class))).thenReturn(generate(() -> kontaktskjema()).limit(maksInnsendingerPerTiMin).collect(toList()));
        assertThat(kontaktskjemaController.meldInteresse(kontaktskjema()).getStatusCode(), is(HttpStatus.TOO_MANY_REQUESTS));
    }

    @Test
    public void skalReturnereStatus200DersomOK() {
        assertThat(kontaktskjemaController.meldInteresse(kontaktskjema()).getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void skalSendeMetrikkOmVellykketMottattKontaktskjema() {
        Kontaktskjema kontaktskjema = kontaktskjema();
        kontaktskjemaController.meldInteresse(kontaktskjema);

        verify(eventPublisher, times(1)).publishEvent(new BesvarelseMottatt(true, kontaktskjema));
    }

    @Test
    public void skalSendeMetrikkOmFeiletKontaktskjema() {
        KontaktskjemaRepository repository = mock(KontaktskjemaRepository.class);
        when(repository.save(any())).thenThrow(KontaktskjemaException.class);
        KontaktskjemaController kontaktskjemaController = new KontaktskjemaController(repository, maksInnsendingerPerTiMin, eventPublisher, unleash);
        Kontaktskjema kontaktskjema = kontaktskjema();

        kontaktskjemaController.meldInteresse(kontaktskjema);

        verify(eventPublisher, times(1)).publishEvent(new BesvarelseMottatt(false, kontaktskjema));
    }
}
