package no.nav.tag.kontakt.oss.salesforce;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.events.BesvarelseMottatt;
import no.nav.tag.kontakt.oss.featureToggles.FeatureToggleService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class SalesforceEventListener {

    private final static List<String> pilotfylker = Arrays.asList(
            "0400", // Innlandet
            "0200", // Øst-Viken
            "1000", // Agder
            "0300"  // Oslo
    );

    private final SalesforceKlient salesforceKlient;
    private final FeatureToggleService featureToggles;

    public SalesforceEventListener(SalesforceKlient salesforceKlient, FeatureToggleService featureToggleService) {
        this.salesforceKlient = salesforceKlient;
        this.featureToggles = featureToggleService;
    }

    @EventListener
    public void besvarelseMottatt(BesvarelseMottatt event) {
        boolean erEnabled = featureToggles.erEnabled("tag.kontakt-oss-api.send-til-salesforce");
        if (!erEnabled) {
            return;
        }

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
