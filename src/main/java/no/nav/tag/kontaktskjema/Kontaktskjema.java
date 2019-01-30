package no.nav.tag.kontaktskjema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
