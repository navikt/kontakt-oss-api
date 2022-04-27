package no.nav.arbeidsgiver.kontakt.oss.fylkesinndeling;

public class KommuneEllerBydel {
    private String nummer;
    private String navn;

    public KommuneEllerBydel(){};

    public KommuneEllerBydel(String nummer, String navn) {
        this.nummer = nummer;
        this.navn = navn;
    }

    public String getNummer() {
        return nummer;
    }

    public void setNummer(String nummer) {
        this.nummer = nummer;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }
}
