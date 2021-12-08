package no.nav.arbeidsgiver.kontakt.oss;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import no.nav.arbeidsgiver.kontakt.oss.events.BesvarelseMottatt;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.KommuneEllerBydel;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.LokasjonsValidator;
import no.nav.arbeidsgiver.kontakt.oss.kafka.utsending.KontaktskjemaUtsendingRepository;
import no.nav.arbeidsgiver.kontakt.oss.testUtils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.generate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KontaktskjemaServiceTest {

    private int maksInnsendingerPerTiMin = 10;

    @Mock
    private KontaktskjemaRepository repository;

    @Mock
    private KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private DateProvider dateProvider;

    @Mock
    private LokasjonsValidator lokasjonsValidator;

    private KontaktskjemaService kontaktskjemaService;

    @BeforeEach
    public void setUp() {
        kontaktskjemaService = new KontaktskjemaService(
                maksInnsendingerPerTiMin,
                repository,
                kontaktskjemaUtsendingRepository,
                eventPublisher,
                dateProvider,
                new KontaktskjemaValidator(lokasjonsValidator)
        );
    }

    private void mockLagretKontaktskjema() {
        Kontaktskjema lagretKontaktskjema = TestData.kontaktskjema();
        lagretKontaktskjema.setId(132);
        when(repository.save(any())).thenReturn(lagretKontaktskjema);
    }

    @Test
    public void harMottattForMangeInnsendinger__skal_returnere_true_hvis_for_mange_innsendinger() {
        when(repository.findAllNewerThan(any(LocalDateTime.class)))
                .thenReturn(generate(() -> TestData.kontaktskjema()).limit(10).collect(toList()));
        assertThat(kontaktskjemaService.harMottattForMangeInnsendinger()).isTrue();
    }

    @Test
    public void harMottattForMangeInnsendinger__skal_returnere_false_hvis_ikke_for_mange_innsendinger() {
        when(repository.findAllNewerThan(any(LocalDateTime.class)))
                .thenReturn(generate(() -> TestData.kontaktskjema()).limit(9).collect(toList()));
        assertThat(kontaktskjemaService.harMottattForMangeInnsendinger()).isFalse();
    }

    @Test
    public void lagreKontaktskjema__skal_lagre_kontaktskjema_i_repo() {
        mockLagretKontaktskjema();
        Kontaktskjema kontaktskjema = TestData.kontaktskjema();
        kontaktskjemaService.saveFormSubmission(kontaktskjema);
        verify(repository).save(kontaktskjema);
    }

    @Test
    public void lagreKontaktskjema__skal_sette_dato() {
        mockLagretKontaktskjema();
        LocalDateTime now = now();
        Kontaktskjema kontaktskjema = TestData.kontaktskjema();
        when(dateProvider.now()).thenReturn(now);
        kontaktskjemaService.saveFormSubmission(kontaktskjema);
        assertThat(kontaktskjema.getOpprettet()).isEqualTo(now);
    }

    @Test
    public void lagreKontaktskjema__skal_sende_event_hvis_vellykket_innsending() {
        mockLagretKontaktskjema();
        Kontaktskjema kontaktskjema = TestData.kontaktskjema();
        kontaktskjemaService.saveFormSubmission(kontaktskjema);

        verify(eventPublisher).publishEvent(new BesvarelseMottatt(true, kontaktskjema));
    }

    @Test
    public void lagreKontaktskjema__skal_sende_event_hvis_feilet_innsending() {
        when(repository.save(any())).thenThrow(KontaktskjemaException.class);
        Kontaktskjema kontaktskjema = TestData.kontaktskjema();

        try {
            kontaktskjemaService.saveFormSubmission(kontaktskjema);
        } catch (Exception ignored) {
        }

        verify(eventPublisher, times(1)).publishEvent(new BesvarelseMottatt(false, kontaktskjema));
    }

    @Test
    public void lagreKontaktskjema__skal_sende_event_ved_feilvalidering() {
        doThrow(KontaktskjemaException.class).when(lokasjonsValidator).validerKommunenr(anyString());
        Kontaktskjema kontaktskjema = TestData.kontaktskjema();
        kontaktskjema.setTemaType(TemaType.REKRUTTERING);

        try {
            kontaktskjemaService.saveFormSubmission(kontaktskjema);
        } catch (Exception ignored) {
        }

        verify(eventPublisher, times(1)).publishEvent(new BesvarelseMottatt(false, kontaktskjema));
    }

    @Test
    public void lagreKontaktskjema__skal_feile_hvis_tematype_IKKE_er_forebygge_sykefravær_og_kommunenr_er_ugyldig() {
        doThrow(KontaktskjemaException.class).when(lokasjonsValidator).validerKommunenr("1234");

        Kontaktskjema kontaktskjema = TestData.kontaktskjema();
        kontaktskjema.setTemaType(TemaType.REKRUTTERING);
        kontaktskjema.setKommunenr("1234");

        assertThrows(
                BadRequestException.class,
                () -> kontaktskjemaService.saveFormSubmission(kontaktskjema)
        );
    }

    @Test
    public void lagreKontaktskjema__skal_fungere_hvis_tematype_IKKE_er_forebygge_sykefravær_og_kommunenr_er_gyldig() {
        mockLagretKontaktskjema();

        Kontaktskjema kontaktskjema = TestData.kontaktskjema();
        kontaktskjema.setTemaType(TemaType.REKRUTTERING);
        kontaktskjema.setKommunenr("1234");

        kontaktskjemaService.saveFormSubmission(kontaktskjema);
    }

    @Test
    public void lagreKontaktskjema__skal_feile_hvis_tematype_er_forebygge_sykefravær_og_fylke_er_ugyldig() {
        doThrow(KontaktskjemaException.class).when(lokasjonsValidator).validerFylkesenhetnr("1234");

        Kontaktskjema kontaktskjema = TestData.kontaktskjema();
        kontaktskjema.setTemaType(TemaType.FOREBYGGE_SYKEFRAVÆR);
        kontaktskjema.setFylkesenhetsnr("1234");

        assertThrows(
                BadRequestException.class,
                () -> kontaktskjemaService.saveFormSubmission(kontaktskjema)
        );
    }

    @Test
    public void lagreKontaktskjema__skal_fungere_hvis_tematype_er_forebygge_sykefravær_og_fylke_er_gyldig() {
        mockLagretKontaktskjema();

        Kontaktskjema kontaktskjema = TestData.kontaktskjema();
        kontaktskjema.setTemaType(TemaType.FOREBYGGE_SYKEFRAVÆR);
        kontaktskjema.setFylkesenhetsnr("1234");

        kontaktskjemaService.saveFormSubmission(kontaktskjema);
    }

    @Test
    @SneakyThrows
    public void lagreKontaktskjema__skal_returnere_hvis_kontaktskjema_er_gyldig() {
        mockLagretKontaktskjema();
        Kontaktskjema gyldigKontaktskjema = TestData.kontaktskjemaBuilder()
                .fornavn("Per")
                .etternavn("Persén")
                .bedriftsnavn("Årvõll Øks3sk4ft")
                .epost("hei@årvoll.øks3-sk4ft.no")
                .telefonnr("+47 99 99 99 99")
                .orgnr("979312059")
                .build();

        kontaktskjemaService.saveFormSubmission(gyldigKontaktskjema);
    }

    @SneakyThrows
    @Test
    public void lagreKontaktskjema__skal_feile_ved_ugyldig_bedriftsnavn() {
        Kontaktskjema ugyldigKontaktskjema = TestData.kontaktskjemaBuilder()
                .bedriftsnavn("$jokoladefabrikken")
                .build();
        assertThrows(
                BadRequestException.class,
                () -> kontaktskjemaService.saveFormSubmission(ugyldigKontaktskjema)
        );
    }

    @SneakyThrows
    @Test
    public void lagreKontaktskjema__skal_feile_ved_ugyldig_telefonnumer() {
        Kontaktskjema ugyldigKontaktskjema = TestData.kontaktskjemaBuilder()
                .telefonnr("abcde")
                .build();

        assertThrows(
                BadRequestException.class,
                () -> kontaktskjemaService.saveFormSubmission(ugyldigKontaktskjema)
        );

    }

    @SneakyThrows
    @Test
    public void lagreKontaktskjema__skal_feile_ved_ugyldig_epost() {
        Kontaktskjema ugyldigKontaktskjema = TestData.kontaktskjemaBuilder()
                .epost("hei@$jokoladefabrikken.no")
                .build();

        assertThrows(
                BadRequestException.class,
                () -> kontaktskjemaService.saveFormSubmission(ugyldigKontaktskjema)
        );
    }

    @SneakyThrows
    @Test
    public void lagreKontaktskjema__skal_feile_ved_ugyldig_orgnummer() {
        Kontaktskjema ugyldigKontaktskjema = TestData.kontaktskjemaBuilder()
                .orgnr("abcde")
                .build();
        assertThrows(
                BadRequestException.class,
                () -> kontaktskjemaService.saveFormSubmission(ugyldigKontaktskjema)
        );
    }

    @SneakyThrows
    @Test
    public void lagreKontaktskjema__skal_akseptere_understrek_i_epost() {
        mockLagretKontaktskjema();
        Kontaktskjema ugyldigKontaktskjema = TestData.kontaktskjemaBuilder()
                .epost("hei_hei@nav.no")
                .build();
        kontaktskjemaService.saveFormSubmission(ugyldigKontaktskjema);
    }

    @SneakyThrows
    @Test
    public void lagreKontaktskjema__skal_akseptere_skråstrek_og_parenteser_i_bedriftsnavn() {
        mockLagretKontaktskjema();
        Kontaktskjema ugyldigKontaktskjema = TestData.kontaktskjemaBuilder()
                .bedriftsnavn("Mark AS (egen bedrift) / Krok ENK (konas bedrift)")
                .build();
        kontaktskjemaService.saveFormSubmission(ugyldigKontaktskjema);
    }

    @SneakyThrows
    @Test
    public void lagreKontaktskjema_skal_ta_imot_alle_kommuner() {
        mockLagretKontaktskjema();
        hentAlleKommunenavnFraMock()
                .forEach(kommune -> kontaktskjemaService.saveFormSubmission(
                        TestData.kontaktskjemaBuilder().kommune(kommune).build())
                );
    }

    @SneakyThrows
    @Test
    public void lagreKontaktskjema_skal_ta_imot_kontaktskjema_uten_orgnr() {
        mockLagretKontaktskjema();
        kontaktskjemaService.saveFormSubmission(TestData.kontaktskjemaBuilder().orgnr(null).build());
        kontaktskjemaService.saveFormSubmission(TestData.kontaktskjemaBuilder().orgnr("").build());
    }

    @SneakyThrows
    private List<String> hentAlleKommunenavnFraMock() {
        String fylkesinndelingJson = TestData.lesFil("mock/fylkesinndeling.json");
        Map<String, List<KommuneEllerBydel>> fylkesinndeling =
                new ObjectMapper().readValue(fylkesinndelingJson, new TypeReference<Map<String, List<KommuneEllerBydel>>>() {
                });
        return fylkesinndeling
                .values()
                .stream()
                .flatMap(List::stream)
                .map(KommuneEllerBydel::getNavn)
                .collect(Collectors.toList());
    }
}
