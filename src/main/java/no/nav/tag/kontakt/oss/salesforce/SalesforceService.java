package no.nav.tag.kontakt.oss.salesforce;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.TemaType;
import no.nav.tag.kontakt.oss.featureToggles.FeatureToggleService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class SalesforceService {

    private final static List<String> pilotfylker = Arrays.asList(
            "0400", // Innlandet
            "0200", // Øst-Viken
            "1000", // Agder
            "0300"  // Oslo
    );

    private final SalesforceKlient salesforceKlient;
    private final FeatureToggleService featureToggles;

    public SalesforceService(SalesforceKlient salesforceKlient, FeatureToggleService featureToggleService) {
        this.salesforceKlient = salesforceKlient;
        this.featureToggles = featureToggleService;
    }

    public void sendKontaktskjemaTilSalesforce(Kontaktskjema kontaktskjema) {
        if (!skalSendeInnSkjema(kontaktskjema)) {
            return;
        }

        salesforceKlient.sendContactFormTilSalesforce(new ContactForm(
                kontaktskjema.getTemaType(),
                kontaktskjema.getKommunenr(),
                kontaktskjema.getBedriftsnavn(),
                kontaktskjema.getOrgnr(),
                kontaktskjema.getFornavn(),
                kontaktskjema.getEtternavn(),
                kontaktskjema.getEpost(),
                kontaktskjema.getTelefonnr()
        ));
    }

    private boolean skalSendeInnSkjema(Kontaktskjema kontaktskjema) {
        return harTemaSomSkalSendesInnTilSalesforce(kontaktskjema)
                && erPilotfylke(kontaktskjema.getFylke())
                && featureToggles.erEnabled("tag.kontakt-oss-api.send-til-salesforce");
    }

    private boolean harTemaSomSkalSendesInnTilSalesforce(Kontaktskjema kontaktskjema) {
        TemaType temaType = kontaktskjema.getTemaType();
        return TemaType.REKRUTTERING.equals(temaType)
                || TemaType.REKRUTTERING_MED_TILRETTELEGGING.equals(temaType)
                || TemaType.ARBEIDSTRENING.equals(temaType);
    }

    private boolean erPilotfylke(String fylkenr) {
        return pilotfylker.contains(fylkenr);
    }
}
