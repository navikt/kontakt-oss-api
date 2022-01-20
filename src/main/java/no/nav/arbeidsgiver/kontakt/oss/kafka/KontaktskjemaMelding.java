package no.nav.arbeidsgiver.kontakt.oss.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;
import no.nav.arbeidsgiver.kontakt.oss.TemaType;


@Data
@AllArgsConstructor
public class KontaktskjemaMelding {
    private TemaType temaType;
    private String fylkesenhetsnr;
    private String kommunenr;
    private String bedriftsnavn;
    private String orgnr;
    private String epost;
    private String telefonnr;
    private String navn;

    public static KontaktskjemaMelding lagKontaktskjemaMelding(Kontaktskjema kontaktskjema) {
        return new KontaktskjemaMelding(
                kontaktskjema.getTemaType(),
                kontaktskjema.getFylkesenhetsnr(),
                kontaktskjema.getKommunenr(),
                kontaktskjema.getBedriftsnavn(),
                kontaktskjema.getOrgnr(),
                kontaktskjema.getEpost(),
                kontaktskjema.getTelefonnr(),
                kontaktskjema.getNavn());
    }
}
