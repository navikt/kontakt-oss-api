package no.nav.tag.kontakt.oss.gsak.integrasjon;

import lombok.Value;

@Value
public class GsakRequest {
    private final String tildeltEnhetsnr;
    private final String beskrivelse;
    private final String temagruppe;
    private final String tema;
    private final String oppgavetype;
    private final String prioritet;
    private final String aktivDato;
    private final String fristFerdigstillelse;
}