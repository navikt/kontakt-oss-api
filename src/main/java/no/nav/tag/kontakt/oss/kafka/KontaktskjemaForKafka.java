package no.nav.tag.kontakt.oss.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.TemaType;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class KontaktskjemaForKafka {

    private Integer id;
    private LocalDateTime opprettet;
    private String fylke;
    private String kommune;
    private String kommunenr;
    private String bedriftsnavn;
    private String orgnr;
    private String tema;
    private TemaType temaType;


    public static KontaktskjemaForKafka kontaktskjemaForKafka(Kontaktskjema kontaktskjema) {
        return KontaktskjemaForKafka.builder()
                .id(kontaktskjema.getId())
                .opprettet(kontaktskjema.getOpprettet())
                .fylke(kontaktskjema.getFylke())
                .kommune(kontaktskjema.getKommune())
                .kommunenr(kontaktskjema.getKommunenr())
                .bedriftsnavn(kontaktskjema.getBedriftsnavn())
                .orgnr(kontaktskjema.getOrgnr())
                .tema(kontaktskjema.getTema())
                .temaType(kontaktskjema.getTemaType())
                .build();
    }
}
