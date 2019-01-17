package no.nav.tag.kontaktskjema;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.generate;
import static no.nav.tag.kontaktskjema.KontaktskjemaController.MAX_INNSENDINGER_PR_TI_MIN;
import static no.nav.tag.kontaktskjema.TestData.lagKontaktskjema;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

public class KontaktskjemaControllerTest {

    private KontaktskjemaRepository repository = Mockito.mock(KontaktskjemaRepository.class);
    private KontaktskjemaController kontaktskjemaController = new KontaktskjemaController(repository);

    @Test(expected = KontaktskjemaException.class)
    public void skalFeileVedLagringAvKontaktskjemaMedForhandsdefinertId() {
        Kontaktskjema kontaktskjema = lagKontaktskjema();
        kontaktskjema.setId(52);
        kontaktskjemaController.meldInteresse(kontaktskjema);
    }

    @Test
    public void skalReturnereStatus500DersomLagringFeiler() {
        when(repository.save(any(Kontaktskjema.class))).thenThrow(new RuntimeException("Feil ved lagring"));
        assertThat(kontaktskjemaController.meldInteresse(lagKontaktskjema()).getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void skalReturnere429VedForMangeInnsendinger() {
        when(repository.findAllNewerThan(any(LocalDateTime.class))).thenReturn(generate(() -> lagKontaktskjema()).limit(MAX_INNSENDINGER_PR_TI_MIN).collect(toList()));
        assertThat(kontaktskjemaController.meldInteresse(lagKontaktskjema()).getStatusCode(), is(HttpStatus.TOO_MANY_REQUESTS));
    }
    
    @Test
    public void skalReturnereStatus200DersomOK() {
        assertThat(kontaktskjemaController.meldInteresse(lagKontaktskjema()).getStatusCode(), is(HttpStatus.OK));
    }


}