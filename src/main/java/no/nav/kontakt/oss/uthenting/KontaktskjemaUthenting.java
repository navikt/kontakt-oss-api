package no.nav.kontakt.oss.uthenting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class KontaktskjemaUthenting {
    private Integer id;
    private LocalDateTime opprettet;
    private String melding;
    private List<String> mottakere;
    private String fylke;
    private String kommune;
    private String kommunenr;
    private String bedriftsnavn;
    private String bedriftsnr;
    private String fornavn;
    private String etternavn;
    private String epost;
    private String telefonnr;
    private String tema;
}
