package no.nav.tag.kontaktskjema;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
public class Kontaktskjema {
    @Id
    private Integer id;
    private LocalDateTime opprettet = LocalDateTime.now();
    private String melding;
    private String fylke;
    private String kommune;
    private String bedriftsnavn;
    private String fornavn;
    private String etternavn;
    private String epost;
    private String telefonnr;
}
