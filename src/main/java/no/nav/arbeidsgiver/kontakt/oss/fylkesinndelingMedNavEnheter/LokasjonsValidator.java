package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter;

import lombok.AllArgsConstructor;
import no.nav.arbeidsgiver.kontakt.oss.KontaktskjemaException;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
@AllArgsConstructor
public class LokasjonsValidator {
    private final FylkesinndelingRepository fylkesinndelingRepository;

    public void validerKommunenr(String kommunenr) {
        boolean eksisterer = fylkesinndelingRepository.alleLokasjoner()
                .stream()
                .map(KommuneEllerBydel::getNummer)
                .anyMatch(Predicate.isEqual(kommunenr));
        if (!eksisterer) {
            throw new KontaktskjemaException("Finner ingen NAV-enhet tilhørende kommune " + kommunenr);
        }
    }

    public void validerFylkesenhetnr(String fylkesenhetsnr) {
        switch (fylkesenhetsnr) {
            case "1000": // Agder
            case "0400": // Innlandet
            case "1800": // Nordland
            case "0200": // Øst-Viken
            case "0800": // Vestfold og Telemark
            case "1200": // Vestland
            case "1900": // Troms og Finnmark
            case "1500": // Møre og Romsdal
            case "0300": // Oslo
            case "1100": // Rogaland
            case "5700": // Trøndelag
            case "0600": // Vest-Viken
                return;

            default:
                throw new KontaktskjemaException("Fant ikke arbeidslivssenter tilhørende fylkesenhet " + fylkesenhetsnr);
        }
    }
}
