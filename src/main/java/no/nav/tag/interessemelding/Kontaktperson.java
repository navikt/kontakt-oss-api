package no.nav.tag.interessemelding;

import lombok.Data;

@Data
public class Kontaktperson {
    private String fornavn;
    private String etternavn;
    private String epost;
    private int telefonnr;
}
