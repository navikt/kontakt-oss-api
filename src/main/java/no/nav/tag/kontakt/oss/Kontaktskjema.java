package no.nav.tag.kontakt.oss;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
    private String fornavn;
    private String etternavn;
    private String epost;
    private String telefonnr;
    private String tema;
    private TemaType temaType;
    private Boolean harSnakketMedAnsattrepresentant;
}
