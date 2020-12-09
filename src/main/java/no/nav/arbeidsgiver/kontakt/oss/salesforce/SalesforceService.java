package no.nav.arbeidsgiver.kontakt.oss.salesforce;

import lombok.extern.slf4j.Slf4j;
import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;
import no.nav.arbeidsgiver.kontakt.oss.featureToggles.FeatureToggleService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SalesforceService {

    private final SalesforceKlient salesforceKlient;

    public SalesforceService(SalesforceKlient salesforceKlient) {
        this.salesforceKlient = salesforceKlient;
    }

    public void sendKontaktskjemaTilSalesforce(Kontaktskjema kontaktskjema) {
        salesforceKlient.sendContactFormTilSalesforce(
                kontaktskjema.getId(),
                new ContactForm(
                        kontaktskjema.getTemaType(),
                        kontaktskjema.getFylkesenhetsnr(),
                        kontaktskjema.getKommunenr(),
                        kontaktskjema.getBedriftsnavn(),
                        kontaktskjema.getOrgnr(),
                        kontaktskjema.getFornavn(),
                        kontaktskjema.getEtternavn(),
                        kontaktskjema.getEpost(),
                        kontaktskjema.getTelefonnr()
                )
        );
    }
}
