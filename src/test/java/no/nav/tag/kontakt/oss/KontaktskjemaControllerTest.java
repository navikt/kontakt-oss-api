package no.nav.tag.kontakt.oss;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import static no.nav.tag.kontakt.oss.testUtils.TestData.kontaktskjema;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KontaktskjemaControllerTest {

    @Mock
    private KontaktskjemaService kontaktskjemaService;

    private KontaktskjemaController kontaktskjemaController;

    @Before
    public void setUp() {
        kontaktskjemaController = new KontaktskjemaController(kontaktskjemaService);
    }

    @Test
    public void meldInteresse__skal_kalle_service() {
        Kontaktskjema kontaktskjema = kontaktskjema();
        kontaktskjemaController.meldInteresse(kontaktskjema);
        verify(kontaktskjemaService).lagreKontaktskjema(kontaktskjema);
    }

    @Test
    public void meldInteresse__skal_returnere_429_ved_for_mange_innsendinger() {
        when(kontaktskjemaService.harMottattForMangeInnsendinger()).thenReturn(true);
        assertThat(kontaktskjemaController.meldInteresse(kontaktskjema()).getStatusCode(), is(HttpStatus.TOO_MANY_REQUESTS));
    }

    @Test
    public void meldInteresse__skal_returnere_200_dersom_OK() {
        assertThat(kontaktskjemaController.meldInteresse(kontaktskjema()).getStatusCode(), is(HttpStatus.OK));
    }

}
