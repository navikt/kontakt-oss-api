package no.nav.tag.kontakt.oss;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.events.BesvarelseMottatt;
import no.nav.tag.kontakt.oss.navenhetsmapping.NavEnhetService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class KontaktskjemaService {
    private final int maksInnsendingerPerTiMin;
    private final KontaktskjemaRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final DateProvider dateProvider;
    private final NavEnhetService navEnhetService;

    public KontaktskjemaService(
            @Value("${kontaktskjema.max-requests-per-10-min}") Integer maksInnsendingerPerTiMin,
            KontaktskjemaRepository repository,
            ApplicationEventPublisher eventPublisher,
            DateProvider dateProvider,
            NavEnhetService navEnhetService) {
        this.maksInnsendingerPerTiMin = maksInnsendingerPerTiMin;
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.dateProvider = dateProvider;
        this.navEnhetService = navEnhetService;
    }

    public void lagreKontaktskjema(Kontaktskjema kontaktskjema) {
        validerKontaktskjema(kontaktskjema);
        kontaktskjema.setOpprettet(dateProvider.now());

        try {
            repository.save(kontaktskjema);
        } catch (Exception e) {
            eventPublisher.publishEvent(new BesvarelseMottatt(false, kontaktskjema));
            throw e;
        }
        log.info("Vellykket innsending.");
        eventPublisher.publishEvent(new BesvarelseMottatt(true, kontaktskjema));
    }

    private void validerKontaktskjema(Kontaktskjema kontaktskjema) {
        try {
            if (TemaType.FOREBYGGE_SYKEFRAVÆR.equals(kontaktskjema.getTemaType())) {
                navEnhetService.mapFraFylkesenhetNrTilArbeidslivssenterEnhetsnr(kontaktskjema.getFylke());
            } else {
                navEnhetService.mapFraKommunenrTilEnhetsnr(kontaktskjema.getKommunenr());
            }
        } catch (KontaktskjemaException e) {
            throw new BadRequestException("Innsendt kontaktskjema er ugyldig", e);
        }
    }

    public boolean harMottattForMangeInnsendinger() {
        return repository.findAllNewerThan(LocalDateTime.now().minusMinutes(10)).size() >= maksInnsendingerPerTiMin;
    }
}
