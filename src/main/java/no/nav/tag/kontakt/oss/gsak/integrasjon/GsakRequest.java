package no.nav.tag.kontakt.oss.gsak.integrasjon;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GsakRequest {
    private String tildeltEnhetsnr;
    private String beskrivelse;
    private String temagruppe;
    private String tema;
    private String oppgavetype;
    private String prioritet;
    private String aktivDato;
    private String fristFerdigstillelse;
}