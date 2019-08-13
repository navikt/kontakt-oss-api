package no.nav.tag.kontakt.oss;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.FylkesinndelingMedNavEnheter;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.KommuneEllerBydel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static no.nav.tag.kontakt.oss.testUtils.TestData.*;
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
                .fornavn("Per")
                .etternavn("Persén")
                .bedriftsnavn("Årvõll Øks3sk4ft")
                .epost("hei@årvoll.øks3-sk4ft.no")
                .telefonnr("+47 99 99 99 99")
                .orgnr("979312059")
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

    @SneakyThrows
    @Test
    public void meldInteresse_skal_ta_imot_alle_kommuner() {
        hentAlleKommunenavnFraMock().forEach(kommune -> {
            kontaktskjemaController.meldInteresse(kontaktskjemaBuilder().kommune(kommune).build());
        });
    }

    @SneakyThrows
    private List<String> hentAlleKommunenavnFraMock() {
        String fylkesinndelingJson = lesFil("mock/fylkesinndeling.json");
        FylkesinndelingMedNavEnheter fylkesinndeling = new FylkesinndelingMedNavEnheter(
                new ObjectMapper().readValue(fylkesinndelingJson, new TypeReference<Map<String, List<KommuneEllerBydel>>>() {})
        );
        return fylkesinndeling
                .getFylkeTilKommuneEllerBydel()
                .values()
                .stream()
                .flatMap(List::stream)
                .map(KommuneEllerBydel::getNavn)
                .collect(Collectors.toList());
    }
}
