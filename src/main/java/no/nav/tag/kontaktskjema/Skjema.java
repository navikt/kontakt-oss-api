package no.nav.tag.kontaktskjema;

import lombok.Data;

@Data
public class Skjema {
    private String fylke;
    private String kommune;
    private String bedriftsnavn;
    private String fornavn;
    private String etternavn;
    private String epost;
    private String telefonnr;
}
