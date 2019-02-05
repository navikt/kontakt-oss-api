package no.nav.tag.kontakt.oss.gsak;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@AllArgsConstructor
@Data
public class GsakInnsending {
    private String tildeltEnhetsnr;
    private String beskrivelse;
    private String temagruppe;
    private String tema;
    private String oppgavetype;
    private String prioritet;
    private LocalDate aktivDato;
    private LocalDate fristFerdigstillelse;
}