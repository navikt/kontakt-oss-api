package no.nav.tag.kontaktskjema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Accessors(chain = true)
public class Kontaktskjema {
    @Id
    private Integer id;
    private LocalDateTime opprettet;
    private String fylke;
    private String kommune;
    private String kommunenr;
    private String bedriftsnavn;
    private String fornavn;
    private String etternavn;
    private String epost;
    private String telefonnr;
}
