package no.nav.tag.kontakt.oss;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.generate;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

public class KontaktskjemaControllerTest {

    private KontaktskjemaRepository repository = Mockito.mock(KontaktskjemaRepository.class);
    private KontaktskjemaController kontaktskjemaController = new KontaktskjemaController(repository);

    @Test
    public void skalLagreKontaktskjemaOk() {
        kontaktskjemaController.meldInteresse(TestData.kontaktskjema());
    }

    @Test
    public void skalReturnereStatus500DersomLagringFeiler() {
        when(repository.save(any(Kontaktskjema.class))).thenThrow(new RuntimeException("Feil ved lagring"));
        assertThat(kontaktskjemaController.meldInteresse(TestData.kontaktskjema()).getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void skalReturnere429VedForMangeInnsendinger() {
        when(repository.findAllNewerThan(any(LocalDateTime.class))).thenReturn(generate(() -> TestData.kontaktskjema()).limit(KontaktskjemaController.MAX_INNSENDINGER_PR_TI_MIN).collect(toList()));
        assertThat(kontaktskjemaController.meldInteresse(TestData.kontaktskjema()).getStatusCode(), is(HttpStatus.TOO_MANY_REQUESTS));
    }
    
    @Test
    public void skalReturnereStatus200DersomOK() {
        assertThat(kontaktskjemaController.meldInteresse(TestData.kontaktskjema()).getStatusCode(), is(HttpStatus.OK));
    }


}