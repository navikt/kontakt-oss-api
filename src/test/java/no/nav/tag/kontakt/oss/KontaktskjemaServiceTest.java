package no.nav.tag.kontakt.oss;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import no.nav.tag.kontakt.oss.events.BesvarelseMottatt;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.FylkesinndelingMedNavEnheter;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.KommuneEllerBydel;
import no.nav.tag.kontakt.oss.navenhetsmapping.NavEnhetService;
import no.nav.tag.kontakt.oss.salesforce.SalesforceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.generate;
import static no.nav.tag.kontakt.oss.testUtils.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KontaktskjemaServiceTest {

    private int maksInnsendingerPerTiMin = 10;

    @Mock
    private KontaktskjemaRepository repository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private DateProvider dateProvider;

    @Mock
    private NavEnhetService navEnhetService;

    @Mock
    private SalesforceService salesforceService;

    private KontaktskjemaService kontaktskjemaService;

    @Before
    public void setUp() {
        kontaktskjemaService = new KontaktskjemaService(
                maksInnsendingerPerTiMin,
                repository,
                eventPublisher,
                dateProvider,
                navEnhetService,
                salesforceService
        );
    }

    @Test
    public void harMottattForMangeInnsendinger__skal_returnere_true_hvis_for_mange_innsendinger() {
        when(repository.findAllNewerThan(any(LocalDateTime.class)))
                .thenReturn(generate(() -> kontaktskjema()).limit(10).collect(toList()));
        assertThat(kontaktskjemaService.harMottattForMangeInnsendinger()).isTrue();
    }

    @Test
    public void harMottattForMangeInnsendinger__skal_returnere_false_hvis_ikke_for_mange_innsendinger() {
        when(repository.findAllNewerThan(any(LocalDateTime.class)))
                .thenReturn(generate(() -> kontaktskjema()).limit(9).collect(toList()));
        assertThat(kontaktskjemaService.harMottattForMangeInnsendinger()).isFalse();
    }

    @Test
    public void lagreKontaktskjema__skal_lagre_kontaktskjema_i_repo() {
        Kontaktskjema kontaktskjema = kontaktskjema();
        kontaktskjemaService.lagreKontaktskjemaOgSendTilSalesforce(kontaktskjema);
        verify(repository).save(kontaktskjema);
    }

    @Test
    public void lagreKontaktskjema__skal_sette_dato() {
        LocalDateTime now = LocalDateTime.now();
        Kontaktskjema kontaktskjema = kontaktskjema();
        when(dateProvider.now()).thenReturn(now);
        kontaktskjemaService.lagreKontaktskjemaOgSendTilSalesforce(kontaktskjema);
        assertThat(kontaktskjema.getOpprettet()).isEqualTo(now);
    }

    @Test
    public void lagreKontaktskjema__skal_sende_event_hvis_vellykket_innsending() {
        Kontaktskjema kontaktskjema = kontaktskjema();
        kontaktskjemaService.lagreKontaktskjemaOgSendTilSalesforce(kontaktskjema);
        verify(eventPublisher).publishEvent(new BesvarelseMottatt(true, kontaktskjema));
    }

    @Test
    public void lagreKontaktskjema__skal_sende_event_hvis_feilet_innsending() {
        when(repository.save(any())).thenThrow(KontaktskjemaException.class);
        Kontaktskjema kontaktskjema = kontaktskjema();

        try {
            kontaktskjemaService.lagreKontaktskjemaOgSendTilSalesforce(kontaktskjema);
        } catch (Exception ignored) {}

        verify(eventPublisher, times(1)).publishEvent(new BesvarelseMottatt(false, kontaktskjema));
    }

    @Test
    public void lagreKontaktskjema__skal_sende_event_ved_feilvalidering() {
        when(navEnhetService.mapFraKommunenrTilEnhetsnr(anyString())).thenThrow(KontaktskjemaException.class);
        Kontaktskjema kontaktskjema = kontaktskjema();
        kontaktskjema.setTemaType(TemaType.REKRUTTERING);

        try {
            kontaktskjemaService.lagreKontaktskjemaOgSendTilSalesforce(kontaktskjema);
        } catch (Exception ignored) {}

        verify(eventPublisher, times(1)).publishEvent(new BesvarelseMottatt(false, kontaktskjema));
    }

    @Test(expected = BadRequestException.class)
    public void lagreKontaktskjema__skal_feile_hvis_tematype_IKKE_er_forebygge_sykefravær_og_kommunenr_er_ugyldig() {
        when(navEnhetService.mapFraKommunenrTilEnhetsnr("1234")).thenThrow(KontaktskjemaException.class);

        Kontaktskjema kontaktskjema = kontaktskjema();
        kontaktskjema.setTemaType(TemaType.REKRUTTERING);
        kontaktskjema.setKommunenr("1234");

        kontaktskjemaService.lagreKontaktskjemaOgSendTilSalesforce(kontaktskjema);
    }

    @Test
    public void lagreKontaktskjema__skal_fungere_hvis_tematype_IKKE_er_forebygge_sykefravær_og_kommunenr_er_gyldig() {
        when(navEnhetService.mapFraKommunenrTilEnhetsnr("1234")).thenReturn("4321");

        Kontaktskjema kontaktskjema = kontaktskjema();
        kontaktskjema.setTemaType(TemaType.REKRUTTERING);
        kontaktskjema.setKommunenr("1234");

        kontaktskjemaService.lagreKontaktskjemaOgSendTilSalesforce(kontaktskjema);
    }

    @Test(expected = BadRequestException.class)
    public void lagreKontaktskjema__skal_feile_hvis_tematype_er_forebygge_sykefravær_og_fylke_er_ugyldig() {
        when(navEnhetService.mapFraFylkesenhetNrTilArbeidslivssenterEnhetsnr("1234")).thenThrow(KontaktskjemaException.class);

        Kontaktskjema kontaktskjema = kontaktskjema();
        kontaktskjema.setTemaType(TemaType.FOREBYGGE_SYKEFRAVÆR);
        kontaktskjema.setFylke("1234");

        kontaktskjemaService.lagreKontaktskjemaOgSendTilSalesforce(kontaktskjema);
    }

    @Test
    public void lagreKontaktskjema__skal_fungere_hvis_tematype_er_forebygge_sykefravær_og_fylke_er_gyldig() {
        when(navEnhetService.mapFraFylkesenhetNrTilArbeidslivssenterEnhetsnr("1234")).thenReturn("4321");

        Kontaktskjema kontaktskjema = kontaktskjema();
        kontaktskjema.setTemaType(TemaType.FOREBYGGE_SYKEFRAVÆR);
        kontaktskjema.setFylke("1234");

        kontaktskjemaService.lagreKontaktskjemaOgSendTilSalesforce(kontaktskjema);
    }


    @Test
    @SneakyThrows
    public void lagreKontaktskjema__skal_returnere_hvis_kontaktskjema_er_gyldig() {
        Kontaktskjema gyldigKontaktskjema = kontaktskjemaBuilder()
                .fornavn("Per")
                .etternavn("Persén")
                .bedriftsnavn("Årvõll Øks3sk4ft")
                .epost("hei@årvoll.øks3-sk4ft.no")
                .telefonnr("+47 99 99 99 99")
                .orgnr("979312059")
                .build();

        kontaktskjemaService.lagreKontaktskjemaOgSendTilSalesforce(gyldigKontaktskjema);
    }

    @SneakyThrows
    @Test(expected = BadRequestException.class)
    public void lagreKontaktskjema__skal_feile_ved_ugyldig_bedriftsnavn() {
        Kontaktskjema ugyldigKontaktskjema = kontaktskjemaBuilder()
                .bedriftsnavn("$jokoladefabrikken")
                .build();

        kontaktskjemaService.lagreKontaktskjemaOgSendTilSalesforce(ugyldigKontaktskjema);
    }

    @SneakyThrows
    @Test(expected = BadRequestException.class)
    public void lagreKontaktskjema__skal_feile_ved_ugyldig_telefonnumer() {
        Kontaktskjema ugyldigKontaktskjema = kontaktskjemaBuilder()
                .telefonnr("abcde")
                .build();

        kontaktskjemaService.lagreKontaktskjemaOgSendTilSalesforce(ugyldigKontaktskjema);
    }

    @SneakyThrows
    @Test(expected = BadRequestException.class)
    public void lagreKontaktskjema__skal_feile_ved_ugyldig_epost() {
        Kontaktskjema ugyldigKontaktskjema = kontaktskjemaBuilder()
                .epost("hei@$jokoladefabrikken.no")
                .build();

        kontaktskjemaService.lagreKontaktskjemaOgSendTilSalesforce(ugyldigKontaktskjema);
    }

    @SneakyThrows
    @Test(expected = BadRequestException.class)
    public void lagreKontaktskjema__skal_feile_ved_ugyldig_orgnummer() {
        Kontaktskjema ugyldigKontaktskjema = kontaktskjemaBuilder()
                .orgnr("abcde")
                .build();

        kontaktskjemaService.lagreKontaktskjemaOgSendTilSalesforce(ugyldigKontaktskjema);
    }

    @SneakyThrows
    @Test
    public void lagreKontaktskjema__skal_akseptere_understrek_i_epost() {
        Kontaktskjema ugyldigKontaktskjema = kontaktskjemaBuilder()
                .epost("hei_hei@nav.no")
                .build();
        kontaktskjemaService.lagreKontaktskjemaOgSendTilSalesforce(ugyldigKontaktskjema);
    }

    @SneakyThrows
    @Test
    public void lagreKontaktskjema__skal_akseptere_skråstrek_og_parenteser_i_bedriftsnavn() {
        Kontaktskjema ugyldigKontaktskjema = kontaktskjemaBuilder()
                .bedriftsnavn("Mark AS (egen bedrift) / Krok ENK (konas bedrift)")
                .build();
        kontaktskjemaService.lagreKontaktskjemaOgSendTilSalesforce(ugyldigKontaktskjema);
    }

    @SneakyThrows
    @Test
    public void lagreKontaktskjema_skal_ta_imot_alle_kommuner() {
        hentAlleKommunenavnFraMock()
                .forEach(kommune -> kontaktskjemaService.lagreKontaktskjemaOgSendTilSalesforce(
                        kontaktskjemaBuilder().kommune(kommune).build())
                );
    }

    @SneakyThrows
    @Test
    public void lagreKontaktskjema_skal_ta_imot_kontaktskjema_uten_orgnr() {
        kontaktskjemaService.lagreKontaktskjemaOgSendTilSalesforce(kontaktskjemaBuilder().orgnr(null).build());
        kontaktskjemaService.lagreKontaktskjemaOgSendTilSalesforce(kontaktskjemaBuilder().orgnr("").build());
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
