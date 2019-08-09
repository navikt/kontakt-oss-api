package no.nav.tag.kontakt.oss;

import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import static no.nav.tag.kontakt.oss.testUtils.TestData.kontaktskjema;
import static no.nav.tag.kontakt.oss.testUtils.TestData.kontaktskjemaBuilder;
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

    @Test
    @SneakyThrows
    public void meldInteresse__skal_returnere_hvis_kontaktskjema_er_gyldig() {
        Kontaktskjema gyldigKontaktskjema = kontaktskjemaBuilder()
                .bedriftsnavn("Årvõll Øks3sk4ft")
                .epost("hei@årvoll.øks3-sk4ft.no")
                .telefonnr("+47 99 99 99 99")
                .orgnr("123 456 789")
                .build();

        assertThat(kontaktskjemaController.meldInteresse(gyldigKontaktskjema).getStatusCode(), is(HttpStatus.OK));
    }

    @SneakyThrows
    @Test(expected = BadRequestException.class)
    public void meldInteresse__skal_returnere_400_ved_ugyldig_bedriftsnavn() {
        Kontaktskjema ugyldigKontaktskjema = kontaktskjemaBuilder()
                .bedriftsnavn("$jokoladefabrikken")
                .build();

        assertThat(kontaktskjemaController.meldInteresse(ugyldigKontaktskjema).getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @SneakyThrows
    @Test(expected = BadRequestException.class)
    public void meldInteresse__skal_returnere_400_ved_ugyldig_telefonnumer() {
        Kontaktskjema ugyldigKontaktskjema = kontaktskjemaBuilder()
                .telefonnr("abcde")
                .build();

        assertThat(kontaktskjemaController.meldInteresse(ugyldigKontaktskjema).getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @SneakyThrows
    @Test(expected = BadRequestException.class)
    public void meldInteresse__skal_returnere_400_ved_ugyldig_epost() {
        Kontaktskjema ugyldigKontaktskjema = kontaktskjemaBuilder()
                .epost("hei@$jokoladefabrikken.no")
                .build();

        assertThat(kontaktskjemaController.meldInteresse(ugyldigKontaktskjema).getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @SneakyThrows
    @Test(expected = BadRequestException.class)
    public void meldInteresse__skal_returnere_400_ved_ugyldig_orgnummer() {
        Kontaktskjema ugyldigKontaktskjema = kontaktskjemaBuilder()
                .orgnr("abcde")
                .build();

        assertThat(kontaktskjemaController.meldInteresse(ugyldigKontaktskjema).getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }
}
