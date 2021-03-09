package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter;

public class Bydel extends KommuneEllerBydel {
    public Bydel(String nummer, String navn) {
        super(nummer, navn);
    }

    public String extractKommunenr() {
        return getNummer().substring(0, 4);
    }

    public Bydel medKommunenavn(Kommune kommune) {
        return new Bydel(
                this.getNummer(),
                kommune.getNavn() + " - " + this.getNavn()
        );
    }
}
