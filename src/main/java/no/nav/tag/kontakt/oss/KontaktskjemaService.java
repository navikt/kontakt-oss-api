package no.nav.tag.kontakt.oss;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.events.BesvarelseMottatt;
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

    public KontaktskjemaService(
            @Value("${kontaktskjema.max-requests-per-10-min}") Integer maksInnsendingerPerTiMin,
            KontaktskjemaRepository repository,
            ApplicationEventPublisher eventPublisher,
            DateProvider dateProvider
    ) {
        this.maksInnsendingerPerTiMin = maksInnsendingerPerTiMin;
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.dateProvider = dateProvider;
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
        if (!true) {
            throw new BadRequestException("ikke gyldig :O");
        }
    }

    public boolean harMottattForMangeInnsendinger() {
        return repository.findAllNewerThan(LocalDateTime.now().minusMinutes(10)).size() >= maksInnsendingerPerTiMin;
    }
}
