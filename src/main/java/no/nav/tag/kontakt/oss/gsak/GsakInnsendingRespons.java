package no.nav.tag.kontakt.oss.gsak;

import lombok.Data;

@Data
public class GsakInnsendingRespons {
    private Integer id;
    private String tildeltEnhetsnr;
    private String beskrivelse;
    private String temagruppe;
    private String tema;
    private String oppgavetype;
    private String versjon;
    private String fristFerdigstillelse;
    private String aktivDato;
    private String opprettetTidspunkt;
    private String opprettetAv;
    private String prioritet;
    private String status;
    private String metadata;
}
