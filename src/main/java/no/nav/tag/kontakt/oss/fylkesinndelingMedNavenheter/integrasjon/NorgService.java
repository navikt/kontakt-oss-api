package no.nav.tag.kontakt.oss.fylkesinndelingMedNavenheter.integrasjon;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode
@Getter
public class NorgService {
    private Map<String, List<KommuneEllerBydel>> geografiMap;

    public NorgService(List<NorgGeografi> norgGeografi) {
        Map<String, List<KommuneEllerBydel>> geografiMap = new HashMap<>();
        geografiMap.put("alle", hentKommunerOgBydeler(norgGeografi));
        this.geografiMap = geografiMap;
    }

    private List<KommuneEllerBydel> hentKommunerOgBydeler(List<NorgGeografi> norgGeografiListe) {
        List<Kommune> kommuner = norgGeografiListe.stream()
                .filter(this::harIkkeNull)
                .filter(norgGeo -> erKommunenr(norgGeo.getNavn()))
                .map(norgGeo -> new Kommune(norgGeo.getNavn(), norgGeo.getTerm()))
                .collect(Collectors.toList());

        List<Bydel> bydeler = norgGeografiListe.stream()
                .filter(this::harIkkeNull)
                .filter(norgGeo -> erBydelsnr(norgGeo.getNavn()))
                .map(norgGeo -> new Bydel(norgGeo.getNavn(), norgGeo.getTerm()))
                .collect(Collectors.toList());

        return lagNyListeDerKommunerSomHarBydelerBlirErstattetMedBydelene(kommuner, bydeler);
    }

    private List<KommuneEllerBydel> lagNyListeDerKommunerSomHarBydelerBlirErstattetMedBydelene(List<Kommune> kommuner, List<Bydel> bydeler) {
        List<KommuneEllerBydel> kommunerOgBydeler = new ArrayList<>();
        List<Kommune> kommunerSomHarBydeler = new ArrayList<>();

        bydeler.forEach(bydel -> {
            String bydelensKommunenr = bydel.getNummer().substring(0, 4);
            Optional<Kommune> kommune = finnKommune(bydelensKommunenr, kommuner);
            if (kommune.isPresent()) {
                Bydel bydelMedOppdatertNavn = new Bydel(bydel.getNummer(), kommune.get().getNavn() + "–" + bydel.getNavn());
                kommunerOgBydeler.add(bydelMedOppdatertNavn);
                kommunerSomHarBydeler.add(kommune.get());
            }
        });

        List<KommuneEllerBydel> kommunerUtenBydeler = new ArrayList<>(kommuner).stream()
                .filter(kommune -> !kommunerSomHarBydeler.contains(kommune))
                .collect(Collectors.toList());
        kommunerOgBydeler.addAll(kommunerUtenBydeler);

        return kommunerOgBydeler;
    }

    private Optional<Kommune> finnKommune(String kommunenummer, List<Kommune> kommuner) {
        return kommuner.stream()
                .filter(kommune -> kommune.getNummer().equals(kommunenummer))
                .findFirst();
    }

    private boolean harIkkeNull(NorgGeografi norgGeografi) {
        return norgGeografi.getNavn() != null && norgGeografi.getTerm() != null;
    }

    private boolean erFylkesnr(String str) {
        return (str.length() == 2) && inneholderBareTall(str);
    }

    private boolean erKommunenr(String str) {
        return (str.length() == 4) && inneholderBareTall(str);
    }

    private boolean erBydelsnr(String str) {
        return (str.length() == 6) && inneholderBareTall(str);
    }

    private boolean inneholderBareTall(String str) {
        return str.matches("[0-9]+");
    }
}