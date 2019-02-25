package no.nav.tag.kontakt.oss.geografi;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NorgOrganisering {
    private String enhetNr;
    private String status;
    private String overordnetEnhet;
}
