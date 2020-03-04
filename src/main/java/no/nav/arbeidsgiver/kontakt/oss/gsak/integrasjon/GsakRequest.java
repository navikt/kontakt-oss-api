package no.nav.arbeidsgiver.kontakt.oss.gsak.integrasjon;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GsakRequest {
    private final String tildeltEnhetsnr;
    private final String opprettetAvEnhetsnr;
    private String orgnr;
    private final String beskrivelse;
    private final String temagruppe;
    private final String tema;
    private final String oppgavetype;
    private final String prioritet;
    private final String aktivDato;
    private final String fristFerdigstillelse;

}
