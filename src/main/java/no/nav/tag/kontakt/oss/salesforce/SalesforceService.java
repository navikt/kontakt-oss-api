package no.nav.tag.kontakt.oss.salesforce;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.Kontaktskjema;
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
        boolean erEnabled = featureToggles.erEnabled("tag.kontakt-oss-api.send-til-salesforce");

        if (!erEnabled) {
            return;
        }

        // TODO Skal ikke sende med kontaktskjema hvis tema er forebygging av sykefravær?

        if (erPilotfylke(kontaktskjema.getFylke())) {
            ContactForm contactForm = new ContactForm(
                    kontaktskjema.getTemaType(),
                    kontaktskjema.getKommunenr(),
                    kontaktskjema.getBedriftsnavn(),
                    kontaktskjema.getOrgnr(),
                    kontaktskjema.getFornavn(),
                    kontaktskjema.getEtternavn(),
                    kontaktskjema.getEpost(),
                    kontaktskjema.getTelefonnr()
            );

            salesforceKlient.sendContactFormTilSalesforce(contactForm);
        }
    }

    private boolean erPilotfylke(String fylkenr) {
        return pilotfylker.contains(fylkenr);
    }
}
