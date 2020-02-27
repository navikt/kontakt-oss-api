package no.nav.tag.kontakt.oss.salesforce;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.featureToggles.FeatureToggleService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SalesforceService {

    private final SalesforceKlient salesforceKlient;
    private final FeatureToggleService featureToggles;

    public SalesforceService(SalesforceKlient salesforceKlient, FeatureToggleService featureToggleService) {
        this.salesforceKlient = salesforceKlient;
        this.featureToggles = featureToggleService;
    }

    public void sendKontaktskjemaTilSalesforce(Kontaktskjema kontaktskjema) {
        if (!featureToggles.erEnabled("tag.kontakt-oss-api.send-til-salesforce")) {
            return;
        }

        salesforceKlient.sendContactFormTilSalesforce(new ContactForm(
                kontaktskjema.getTemaType(),
                kontaktskjema.getFylkesenhetsnr(),
                kontaktskjema.getKommunenr(),
                kontaktskjema.getBedriftsnavn(),
                kontaktskjema.getOrgnr(),
                kontaktskjema.getFornavn(),
                kontaktskjema.getEtternavn(),
                kontaktskjema.getEpost(),
                kontaktskjema.getTelefonnr()
        ));
    }
}
