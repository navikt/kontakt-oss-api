package no.nav.arbeidsgiver.kontakt.oss.navenhetsmapping;

import no.nav.arbeidsgiver.kontakt.oss.KontaktskjemaException;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.FylkesinndelingRepository;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.NavEnhet;
import org.springframework.stereotype.Component;

@Component
public class NavEnhetService {

    private final FylkesinndelingRepository fylkesinndelingRepository;

    public NavEnhetService(FylkesinndelingRepository fylkesinndelingRepository) {
        this.fylkesinndelingRepository = fylkesinndelingRepository;
    }

    public String mapFraKommunenrTilEnhetsnr(String kommunenr) {
        NavEnhet navEnhet = fylkesinndelingRepository
                .hentKommuneNrEllerBydelNrTilNavEnhet()
                .get(kommunenr);
        if (navEnhet != null) {
            return navEnhet.getEnhetNr();
        } else {
            throw new KontaktskjemaException("Finner ingen NAV-enhet tilhørende kommune " + kommunenr);
        }
    }

    public String mapFraFylkesenhetNrTilArbeidslivssenterEnhetsnr(String fylkesenhetsnr) {
        // TODO Manuell mapping foreløpig, fikses i TAG-557
        switch (fylkesenhetsnr) {
            case "1000": // Agder
                return "1091"; // NAV Arbeidslivssenter Agder

            case "0400": // Innlandet
                return "0491"; // NAV Arbeidslivssenter Innlandet, IKKE NAV Arbeidsrådgivning Innlandet (0496)

            case "1800": // Nordland
                return "1891"; // NAV Arbeidslivssenter Nordland

            case "0200": // Øst-Viken
                return "0291"; // NAV Arbeidslivssenter Øst-Viken

            case "0800": // Vestfold og Telemark
                return "0891"; // NAV Arbeid Vestfold og Telemark

            case "1200": // Vestland
                return "1291"; // NAV Arbeidslivssenter Vestland

            case "1900": // Troms og Finnmark
                return "1991"; // NAV Arbeidslivssenter Troms og Finnmark

            case "1500": // Møre og Romsdal
                return "1591"; // NAV Arbeidslivssenter Møre og Romsdal

            case "0300": // Oslo
                return "0391"; // NAV Arbeidslivssenter Oslo

            case "1100": // Rogaland
                return "1191"; // NAV Arbeidslivssenter Rogaland, IKKE NAV Arbeidslivsenter Rogaland, Haugesund (1192)

            case "5700": // Trøndelag
                return "5772"; // NAV Arbeidslivssenter Trøndelag

            case "0600": // Vest-Viken
                return "0691";

            default:
                throw new KontaktskjemaException("Fant ikke arbeidslivssenter tilhørende fylkesenhet " + fylkesenhetsnr);
        }
    }
}
