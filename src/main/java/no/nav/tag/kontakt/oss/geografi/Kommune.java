package no.nav.tag.kontakt.oss.geografi;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
public class Kommune implements KommuneEllerBydel {
    private String nummer;
    private String navn;
}
