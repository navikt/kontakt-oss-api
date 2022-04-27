package no.nav.arbeidsgiver.kontakt.oss;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class Kontaktskjema {

    @Id
    @JsonIgnore
    private Integer id;
    @JsonIgnore
    private LocalDateTime opprettet;

    private Boolean harSnakketMedAnsattrepresentant;
    private String fylkesenhetsnr;
    private String bedriftsnavn;
    private TemaType temaType;
    private String telefonnr;
    private String kommunenr;
    private String kommune;
    private String orgnr;
    private String epost;
    private String tema;
    private String navn;

}
