package no.nav.tag.kontakt.oss.geografi.integrasjon;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
@ToString
public class Bydel implements KommuneEllerBydel {
    private String nummer;
    private String navn;
}
