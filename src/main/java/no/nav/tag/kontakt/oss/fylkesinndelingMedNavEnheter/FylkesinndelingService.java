package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.KodeverkKlient;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgGeografi;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgKlient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FylkesinndelingService {
    private final NorgKlient norgKlient;
    private final KodeverkKlient kodeverkKlient;

    public FylkesinndelingService(NorgKlient norgKlient, KodeverkKlient kodeverkKlient) {
        this.norgKlient = norgKlient;
        this.kodeverkKlient = kodeverkKlient;
    }

    public Map<NavEnhet, NavFylkesenhet> hentMapFraNavenhetTilFylkesenhet() {
        return norgKlient.hentOrganiseringFraNorg().stream()
                .filter(norgOrganisering -> "Aktiv".equals(norgOrganisering.getStatus()))
                .filter(norgOrganisering -> norgOrganisering.getOverordnetEnhet() != null)
                .collect(Collectors.toMap(
                        norgOrganisering -> new NavEnhet(norgOrganisering.getEnhetNr()),
                        norgOrganisering -> new NavFylkesenhet(norgOrganisering.getOverordnetEnhet())
                ));
    }

    public Map<KommuneEllerBydel, NavEnhet> hentMapFraKommuneEllerBydelTilNavEnhet(
            List<KommuneEllerBydel> kommunerOgBydeler
    ) {
        return norgKlient.hentMapFraKommuneEllerBydelTilNavenhet(kommunerOgBydeler);
    }

    public List<KommuneEllerBydel> hentListeOverAlleKommunerOgBydeler() {
        return lagNyListeDerKommunerSomHarBydelerBlirErstattetMedBydelene(
                kodeverkKlient.hentKommuner(),
                kodeverkKlient.hentBydeler()
        );
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
}
