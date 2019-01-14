package no.nav.tag.kontaktskjema.uthenting;

import no.nav.tag.kontaktskjema.Kontaktskjema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class UthentingUtils {
    public final static String MELDING = "Arbeidsgiver har sendt henvendelse gjennom Kontaktskjema. Kontakt arbeidsgiver for å avklare hva henvendelsen gjelder. Minner om at arbeidsgiver skal kontaktes innen 48 timer. Husk å registrere henvendelsen som «Kontaktskjema» i Arena (ikke telefonkontakt). Når arbeidsgiver er kontaktet og henvendelsen registrert i Arena skal denne eposten slettes.";
    private final Map<String, List<String>> epostliste;

    @Autowired
    public UthentingUtils(Map<String, List<String>> epostliste) {
        this.epostliste = epostliste;
    }

    public List<KontaktskjemaUthenting> lagSorterteUthentinger(Iterable<Kontaktskjema> kontaktskjemaer) {
        return sorterUthentinger(lagUthentinger(kontaktskjemaer));
    }

    private List<KontaktskjemaUthenting> sorterUthentinger(List<KontaktskjemaUthenting> uthentinger) {
        return uthentinger.stream()
                .sorted(Comparator.comparing(KontaktskjemaUthenting::getId))
                .collect(Collectors.toList());
    }

    private List<KontaktskjemaUthenting> lagUthentinger(Iterable<Kontaktskjema> kontaktskjemaer) {
        List<KontaktskjemaUthenting> uthentinger = new ArrayList<>();
        for (Kontaktskjema kontaktskjema : kontaktskjemaer) {
            uthentinger.add(lagUthenting(kontaktskjema));
        }
        return uthentinger;
    }

    private KontaktskjemaUthenting lagUthenting(Kontaktskjema kontaktskjema) {
        List<String> mottakere = epostliste.get(kontaktskjema.getKommunenr());

        return new KontaktskjemaUthenting(
                kontaktskjema.getId(),
                kontaktskjema.getOpprettet(),
                MELDING,
                mottakere,
                kontaktskjema.getFylke(),
                kontaktskjema.getKommune(),
                kontaktskjema.getKommunenr(),
                kontaktskjema.getBedriftsnavn(),
                kontaktskjema.getFornavn(),
                kontaktskjema.getEtternavn(),
                kontaktskjema.getEpost(),
                kontaktskjema.getTelefonnr()
        );
    }
}
