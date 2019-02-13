package no.nav.tag.kontakt.oss.gsak;

import no.bekk.bekkopen.org.OrganisasjonsnummerCalculator;
import no.nav.tag.kontakt.oss.DateProvider;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.featureToggles.FeatureToggles;
import no.nav.tag.kontakt.oss.navenhetsmapping.NavEnhetUtils;
import no.nav.tag.kontakt.oss.gsak.integrasjon.GsakKlient;
import no.nav.tag.kontakt.oss.gsak.integrasjon.GsakRequest;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static no.nav.tag.kontakt.oss.gsak.GsakOppgave.OppgaveStatus.DISABLED;
import static no.nav.tag.kontakt.oss.gsak.GsakOppgave.OppgaveStatus.OK;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class GsakOppgaveServiceTest {

    private GsakOppgaveRepository oppgaveRepository = mock(GsakOppgaveRepository.class);
    private DateProvider dateProvider = mock(DateProvider.class);
    private FeatureToggles featureToggles = mock(FeatureToggles.class);
    
    @Before
    public void setUp() {
        when(dateProvider.now()).thenReturn(LocalDateTime.now());
        when(featureToggles.isEnabled(anyString())).thenReturn(true);
    }

    @Test
    public void skalOppdatereDatabaseEtterKallTilGsak() {

        GsakOppgaveService gsakOppgaveForSkjema = new GsakOppgaveService(
                oppgaveRepository,
                dateProvider,
                mock(GsakKlient.class),
                mock(NavEnhetUtils.class),
                featureToggles
        );

        Kontaktskjema kontaktskjema = Kontaktskjema.builder().id(5).build();
        gsakOppgaveForSkjema.opprettOppgaveOgLagreStatus(kontaktskjema);

        verify(oppgaveRepository).save(eq(GsakOppgave.builder().gsakId(0).kontaktskjemaId(5).status(OK).opprettet(dateProvider.now()).build()));

    }
    
    @Test
    public void lagInnsendingSkalBeholdeOrgnrHvisGyldig() {

        GsakOppgaveService gsakOppgaveForSkjema = new GsakOppgaveService(
                oppgaveRepository,
                dateProvider,
                mock(GsakKlient.class),
                mock(NavEnhetUtils.class),
                featureToggles
        );
        String orgnr = OrganisasjonsnummerCalculator.getOrganisasjonsnummerList(1).get(0).getValue();
        GsakRequest gsakRequest = gsakOppgaveForSkjema.lagGsakInnsending(new Kontaktskjema(1, null, null, "Kommune", "1234", "bedriftsnavn", orgnr, "fornavn", "etternavn", "epost", "123", "tema"));
        assertThat(gsakRequest.getOrgnr(), equalTo(orgnr));
    }
    
    @Test
    public void lagInnsendingSkalFjerneOrgnrHvisUgyldig() {

        GsakOppgaveService gsakOppgaveForSkjema = new GsakOppgaveService(
                oppgaveRepository,
                dateProvider,
                mock(GsakKlient.class),
                mock(NavEnhetUtils.class),
                featureToggles
        );
        
        GsakRequest gsakRequest = gsakOppgaveForSkjema.lagGsakInnsending(new Kontaktskjema(1, null, null, "Kommune", "1234", "bedriftsnavn", "123", "fornavn", "etternavn", "epost", "123", "tema"));
        assertThat(gsakRequest.getOrgnr(), equalTo(""));
    }

    @Test
    public void skalReturnereDisabledHvisGsakToggleErAv() {
        when(featureToggles.isEnabled(eq("gsak"))).thenReturn(false);

        GsakOppgaveService gsakOppgaveForSkjema = new GsakOppgaveService(
                oppgaveRepository,
                mock(DateProvider.class),
                mock(GsakKlient.class),
                mock(NavEnhetUtils.class),
                featureToggles
        );

        gsakOppgaveForSkjema.opprettOppgaveOgLagreStatus(Kontaktskjema.builder().build());
        verify(oppgaveRepository).save(eq(GsakOppgave.builder().gsakId(null).status(DISABLED).build()));

    }
    
}
