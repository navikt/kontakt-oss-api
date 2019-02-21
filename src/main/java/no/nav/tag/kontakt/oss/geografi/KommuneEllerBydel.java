package no.nav.tag.kontakt.oss.geografi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class KommuneEllerBydel {
    private String nummer;
    private String navn;
}
