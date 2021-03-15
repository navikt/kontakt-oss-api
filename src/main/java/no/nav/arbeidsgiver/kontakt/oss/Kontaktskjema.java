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

    private String fylkesenhetsnr;
    private String kommune;
    private String kommunenr;
    private String bedriftsnavn;
    private String orgnr;

    @Deprecated
    private String fornavn;

    @Deprecated
    private String etternavn;

    private String epost;
    private String telefonnr;
    private String tema;
    private TemaType temaType;
    private Boolean harSnakketMedAnsattrepresentant;
    private String navn;

    public String getNavn() {
        if (navn != null) {
            return navn;
        } else if (fornavn != null && etternavn != null) {
            return fornavn + " " + etternavn;
        } else {
            return null;
        }
    }
}
