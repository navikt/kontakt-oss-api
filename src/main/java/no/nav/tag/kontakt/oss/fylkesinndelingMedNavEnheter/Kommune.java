package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
@ToString
public class Kommune implements KommuneEllerBydel {
    private String nummer;
    private String navn;
}
