package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NorgOrganisering {
    private String enhetNr;
    private String status;
    private String overordnetEnhet;
}
