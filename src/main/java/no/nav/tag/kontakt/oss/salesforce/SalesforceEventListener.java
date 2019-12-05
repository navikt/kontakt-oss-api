package no.nav.tag.kontakt.oss.salesforce;

import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.events.BesvarelseMottatt;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SalesforceEventListener {

    private final SalesforceKlient salesforceKlient;

    public SalesforceEventListener(SalesforceKlient salesforceKlient) {
        this.salesforceKlient = salesforceKlient;
    }

    @EventListener
    public void besvarelseMottatt(BesvarelseMottatt event) {
        if (event.isSuksess()) {
            Kontaktskjema kontaktskjema = event.getKontaktskjema();
            if (erPilotfylke(kontaktskjema.getFylke())) {
                salesforceKlient.sendKontaktskjemaTilSalesforce(kontaktskjema);
            }
        }
    }

    private boolean erPilotfylke(String fylkenr) {
        // TODO implementer
        return true;
    }
}
