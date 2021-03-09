package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter;

import lombok.extern.slf4j.Slf4j;
import no.nav.arbeidsgiver.kontakt.oss.KontaktskjemaException;
import no.nav.arbeidsgiver.kontakt.oss.events.FylkesinndelingOppdatert;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.KodeverkKlient;
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon.NorgKlient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public void oppdaterFylkesinndeling() {
        log.info("Oppdaterer informasjon fra NORG og Kodeverk");

        try {
            List<KommuneEllerBydel> kommunerOgBydeler = hentListeOverAlleKommunerOgBydeler();
            Map<KommuneEllerBydel, NavEnhet> fraKommuneEllerBydelTilNavEnhet = norgKlient.hentMapFraKommuneEllerBydelTilNavenhet(kommunerOgBydeler);

            FylkesinndelingMedNavEnheter fylkesinndeling = new FylkesinndelingMedNavEnheter(
                    hentMapFraNavenhetTilFylkesenhet(),
                    fraKommuneEllerBydelTilNavEnhet,
                    kommunerOgBydeler
            );

            Map<String, NavEnhet> fraKommunenrEllerBydelnrTilNavEnhet = fraKommuneEllerBydelTilNavEnhet
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey().getNummer(),
                            entry -> entry.getValue()
                    ));

            fylkesinndelingRepository.oppdaterInformasjonFraNorg(
                    fylkesinndeling,
                    fraKommunenrEllerBydelnrTilNavEnhet
            );

            eventPublisher.publishEvent(new FylkesinndelingOppdatert(true));
            log.info("Informasjon om fylkesinndeling ble oppdatert");

        } catch (KontaktskjemaException exception) {
            eventPublisher.publishEvent(new FylkesinndelingOppdatert(false));
            throw exception;
        }
    }

    /* public kun pga testing */
    public Map<NavEnhet, NavFylkesenhet> hentMapFraNavenhetTilFylkesenhet() {
        return norgKlient.hentOrganiseringFraNorg()
                .stream()
                .filter(norgOrganisering -> "Aktiv".equals(norgOrganisering.getStatus()))
                .filter(norgOrganisering -> norgOrganisering.getOverordnetEnhet() != null)
                .collect(Collectors.toMap(
                        norgOrganisering -> new NavEnhet(norgOrganisering.getEnhetNr()),
                        norgOrganisering -> new NavFylkesenhet(norgOrganisering.getOverordnetEnhet())
                ));
    }

    /* public kun pga testing */
    public List<KommuneEllerBydel> hentListeOverAlleKommunerOgBydeler() {
        /* kommunenr til bydeler */
        Map<String, List<Bydel>> kommunensBydeler = kodeverkKlient
                .hentBydeler()
                .stream()
                .collect(Collectors.groupingBy(Bydel::extractKommunenr));

        return kodeverkKlient
                .hentKommuner()
                .stream()
                .flatMap(kommune -> {
                    List<Bydel> bydeler = kommunensBydeler.get(kommune.getNummer());
                    if (bydeler == null) {
                        return Stream.of(kommune);
                    } else {
                        return bydeler.stream().map(bydel -> bydel.medKommunenavn(kommune));
                    }
                })
                .collect(Collectors.toList());
    }
}
