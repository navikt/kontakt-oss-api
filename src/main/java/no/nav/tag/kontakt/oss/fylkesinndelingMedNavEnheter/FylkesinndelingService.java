package no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.KontaktskjemaException;
import no.nav.tag.kontakt.oss.events.FylkesinndelingOppdatert;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.KodeverkKlient;
import no.nav.tag.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgKlient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FylkesinndelingService {

    private final NorgKlient norgKlient;
    private final KodeverkKlient kodeverkKlient;
    private final FylkesinndelingRepository fylkesinndelingRepository;
    private final ApplicationEventPublisher eventPublisher;

    public FylkesinndelingService(
            NorgKlient norgKlient,
            KodeverkKlient kodeverkKlient,
            FylkesinndelingRepository fylkesinndelingRepository,
            ApplicationEventPublisher eventPublisher) {
        this.norgKlient = norgKlient;
        this.kodeverkKlient = kodeverkKlient;
        this.fylkesinndelingRepository = fylkesinndelingRepository;
        this.eventPublisher = eventPublisher;
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
                Bydel bydelMedOppdatertNavn = new Bydel(bydel.getNummer(), kommune.get().getNavn() + " - " + bydel.getNavn());
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

    public void oppdaterFylkesinndeling() {
        log.info("Oppdaterer informasjon fra NORG og Kodeverk");

        try {
            List<KommuneEllerBydel> kommunerOgBydeler = hentListeOverAlleKommunerOgBydeler();
            Map<KommuneEllerBydel, NavEnhet> fraKommuneEllerBydelTilNavEnhet = hentMapFraKommuneEllerBydelTilNavEnhet(kommunerOgBydeler);
            FylkesinndelingMedNavEnheter fylkesinndeling = new FylkesinndelingMedNavEnheter(
                    hentMapFraNavenhetTilFylkesenhet(),
                    fraKommuneEllerBydelTilNavEnhet,
                    kommunerOgBydeler
            );

            lagreFylkesinndelingIDatabase(fraKommuneEllerBydelTilNavEnhet, fylkesinndeling);
            eventPublisher.publishEvent(new FylkesinndelingOppdatert(true));
            log.info("Informasjon om fylkesinndeling ble oppdatert");

        } catch (KontaktskjemaException exception) {
            eventPublisher.publishEvent(new FylkesinndelingOppdatert(false));
            throw exception;
        }
    }

    private void lagreFylkesinndelingIDatabase(Map<KommuneEllerBydel, NavEnhet> fraKommuneEllerBydelTilNavEnhet, FylkesinndelingMedNavEnheter fylkesinndeling) {
        fylkesinndelingRepository.oppdaterInformasjonFraNorg(
                fylkesinndeling,
                fraKommuneEllerBydelTilNavEnhet.keySet().stream().collect(Collectors.toMap(
                        KommuneEllerBydel::getNummer,
                        kommunerEllerBydel -> fraKommuneEllerBydelTilNavEnhet.get(kommunerEllerBydel)
                ))
        );
    }
}
