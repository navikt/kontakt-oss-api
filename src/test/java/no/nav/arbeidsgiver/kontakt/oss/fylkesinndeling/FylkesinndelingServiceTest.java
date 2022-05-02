package no.nav.arbeidsgiver.kontakt.oss.fylkesinndeling;


import no.nav.arbeidsgiver.kontakt.oss.KontaktskjemaException;
import no.nav.arbeidsgiver.kontakt.oss.events.FylkesinndelingOppdatert;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndeling.integrasjon.ClassificationCode;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndeling.integrasjon.SSBKlient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class FylkesinndelingServiceTest {

    @Mock
    private SSBKlient ssbKlient;

    @Mock
    private FylkesinndelingRepository fylkesinndelingRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private FylkesinndelingService fylkesinndelingService;


    @BeforeEach
    public void setUp() {
        fylkesinndelingService = new FylkesinndelingService(fylkesinndelingRepository, ssbKlient, eventPublisher);
    }


    @Test
    public void oppdaterFylkesinndeling__skal_publisere_fylkesinndeling_oppdatert_success() throws Exception {
        doReturn(1).when(fylkesinndelingRepository).oppdatereFylkesinndelinger(anyList());

        fylkesinndelingService.oppdatereFylkesinndeling();
        verify(eventPublisher, times(1)).publishEvent(new FylkesinndelingOppdatert(true));
    }

    @Test
    public void oppdaterFylkesinndeling__skal_publisere_fylkesinndeling_oppdatert_feil() throws Exception {
        doThrow(KontaktskjemaException.class)
                .when(ssbKlient)
                .getClassificationCodes("test");
        try {
            fylkesinndelingService.oppdatereFylkesinndeling();
        } catch (KontaktskjemaException exception) {
            verify(eventPublisher, times(1)).publishEvent(new FylkesinndelingOppdatert(false));
        }
    }

    @Test
    public void henteFylkesinndeling__illegal_state_exception() {
        when(fylkesinndelingService.hentFylkesinndelinger()).thenThrow(IllegalStateException.class);
        assertThrows(IllegalStateException.class, ()->fylkesinndelingService.hentFylkesinndelinger());
    }

    @Test
    public void henteFylkesinndeling__success() {
        when(fylkesinndelingService.hentFylkesinndelinger()).thenReturn(Arrays.asList(
                new KommuneEllerBydel("1234", "Oslo")
            ));

        List<KommuneEllerBydel> kommunerOgBydeler = fylkesinndelingService.hentFylkesinndelinger();

        assertThat(kommunerOgBydeler.size() == 1);
        assertThat(kommunerOgBydeler.get(0).getNavn().equals("Oslo"));
        assertThat(kommunerOgBydeler.get(0).getNummer().equals("1234"));

    }

    @Test
    public void mergeListOfMunicipalitiesAndDistricts(){
        List<ClassificationCode> kommuner = new ArrayList<>();
        List<ClassificationCode> bydeler = new ArrayList<>();

        ClassificationCode kommune1 = new ClassificationCode();
        kommune1.setName("Oslo");
        kommune1.setCode("1234");

        ClassificationCode kommune2 = new ClassificationCode();
        kommune2.setName("Bergen");
        kommune2.setCode("5678");

        ClassificationCode bydel = new ClassificationCode();
        bydel.setCode("12345678");
        bydel.setName("Frogner");

        kommuner.add(kommune1);
        kommuner.add(kommune2);
        bydeler.add(bydel);

        List<KommuneEllerBydel> kommunerOfBydeller = fylkesinndelingService.mergeListOfMunicipalitiesAndDistricts(kommuner, bydeler);

        assertThat(kommunerOfBydeller).hasSize(2).extracting(KommuneEllerBydel::getNavn).contains("Oslo- Frogner");
        assertThat(kommunerOfBydeller).hasSize(2).extracting(KommuneEllerBydel::getNavn).contains("Bergen");

    }

}
