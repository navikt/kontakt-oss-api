package no.nav.arbeidsgiver.kontakt.oss.gsak;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@Builder
public class GsakOppgave {

    @Id
    private Integer id;
    private Integer kontaktskjemaId;
    private Integer gsakId;
    private OppgaveStatus status;
    private LocalDateTime opprettet;

    public enum OppgaveStatus {
        OK,
        FEILET,
        SKAL_IKKE_SENDES,
    }
}
