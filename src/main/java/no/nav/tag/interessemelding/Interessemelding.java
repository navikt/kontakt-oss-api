package no.nav.tag.interessemelding;

import lombok.Data;

@Data
public class Interessemelding {
    private String fylke;
    private String kommune;
    private String bedriftsnavn;
    private Kontaktperson kontaktperson;
}
