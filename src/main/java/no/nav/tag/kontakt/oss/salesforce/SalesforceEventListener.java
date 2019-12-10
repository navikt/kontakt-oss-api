package no.nav.tag.kontakt.oss.salesforce;

import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.events.BesvarelseMottatt;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SalesforceEventListener {

    private final static List<String> pilotfylker = Arrays.asList(
            "0400", // Innlandet
            "0200", // Øst-Viken
            "1000", // Agder
            "0300"  // Oslo
    );

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
        return pilotfylker.contains(fylkenr);
    }
}
