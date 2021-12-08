package no.nav.arbeidsgiver.kontakt.oss;

import no.nav.arbeidsgiver.kontakt.oss.testUtils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KontaktskjemaControllerTest {

    @Mock
    private KontaktskjemaService kontaktskjemaService;

    private KontaktskjemaController kontaktskjemaController;

    @BeforeEach
    public void setUp() {
        kontaktskjemaController = new KontaktskjemaController(kontaktskjemaService);
    }

    @Test
    public void meldInteresse__skal_kalle_service() {
        Kontaktskjema kontaktskjema = TestData.kontaktskjema();
        kontaktskjemaController.meldInteresse(kontaktskjema);
        verify(kontaktskjemaService).saveFormSubmission(kontaktskjema);
    }

    @Test
    public void meldInteresse__skal_returnere_429_ved_for_mange_innsendinger() {
        when(kontaktskjemaService.harMottattForMangeInnsendinger()).thenReturn(true);
        assertThat(kontaktskjemaController.meldInteresse(TestData.kontaktskjema()).getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
    }

    @Test
    public void meldInteresse__skal_returnere_200_dersom_OK() {
        assertThat(kontaktskjemaController.meldInteresse(TestData.kontaktskjema()).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
