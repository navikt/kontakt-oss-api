package no.nav.arbeidsgiver.kontakt.oss;

import lombok.extern.slf4j.Slf4j;
import no.nav.arbeidsgiver.kontakt.oss.events.BesvarelseMottatt;
import no.nav.arbeidsgiver.kontakt.oss.salesforce.utsending.KontaktskjemaUtsending;
import no.nav.arbeidsgiver.kontakt.oss.salesforce.utsending.KontaktskjemaUtsendingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class KontaktskjemaService {
    private final int maksInnsendingerPerTiMin;
    private final KontaktskjemaRepository kontaktskjemaRepository;
    private final KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final DateProvider dateProvider;
    private final KontaktskjemaValidator kontaktskjemaValidator;

    public KontaktskjemaService(
            @Value("${kontaktskjema.max-requests-per-10-min}") Integer maksInnsendingerPerTiMin,
            KontaktskjemaRepository kontaktskjemaRepository,
            KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository,
            ApplicationEventPublisher eventPublisher,
            DateProvider dateProvider,
            KontaktskjemaValidator kontaktskjemaValidator
    ) {
        this.maksInnsendingerPerTiMin = maksInnsendingerPerTiMin;
        this.kontaktskjemaRepository = kontaktskjemaRepository;
        this.kontaktskjemaUtsendingRepository = kontaktskjemaUtsendingRepository;
        this.eventPublisher = eventPublisher;
        this.dateProvider = dateProvider;
        this.kontaktskjemaValidator = kontaktskjemaValidator;
    }

    public void lagreKontaktskjemaOgSendTilSalesforce(Kontaktskjema kontaktskjema) {
        try {
            kontaktskjemaValidator.valider(kontaktskjema);
            kontaktskjema.setOpprettet(dateProvider.now());
            Kontaktskjema lagretKontaktskjema = kontaktskjemaRepository.save(kontaktskjema);
            kontaktskjemaUtsendingRepository.save(
                    KontaktskjemaUtsending.klarTilUtsending(
                            lagretKontaktskjema.getId(),
                            dateProvider.now()
                    )
            );
        } catch (Exception e) {
            eventPublisher.publishEvent(new BesvarelseMottatt(false, kontaktskjema));
            throw e;
        }
        eventPublisher.publishEvent(new BesvarelseMottatt(true, kontaktskjema));
    }

    public boolean harMottattForMangeInnsendinger() {
        return kontaktskjemaRepository.findAllNewerThan(LocalDateTime.now().minusMinutes(10)).size() >= maksInnsendingerPerTiMin;
    }
}
