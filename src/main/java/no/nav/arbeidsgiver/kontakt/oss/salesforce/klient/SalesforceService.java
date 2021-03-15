package no.nav.arbeidsgiver.kontakt.oss.salesforce.klient;

import lombok.extern.slf4j.Slf4j;
import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;
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
                        kontaktskjema.getEpost(),
                        kontaktskjema.getTelefonnr(),
                        kontaktskjema.getNavn()
                )
        );
    }
}
