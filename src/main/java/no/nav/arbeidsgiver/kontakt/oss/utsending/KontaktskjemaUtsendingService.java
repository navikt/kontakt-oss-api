package no.nav.arbeidsgiver.kontakt.oss.utsending;

import lombok.extern.slf4j.Slf4j;
import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;
import no.nav.arbeidsgiver.kontakt.oss.kafka.KontaktskjemaKlarTilsending;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
@Slf4j
public class KontaktskjemaUtsendingService {

    private final KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public KontaktskjemaUtsendingService(
            KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository,
            ApplicationEventPublisher publisher
    ) {
        this.kontaktskjemaUtsendingRepository = kontaktskjemaUtsendingRepository;
        this.publisher = publisher;
    }

    @Transactional
    public void sendSkjemaTilKafka(Kontaktskjema skjema) {
        KontaktskjemaUtsending kontaktskjemaUtsending =
                kontaktskjemaUtsendingRepository.hentKontakskjemaUtsending(
                        skjema.getId()
                );
        kontaktskjemaUtsendingRepository.save(KontaktskjemaUtsending.sent(kontaktskjemaUtsending));
        publisher.publishEvent(new KontaktskjemaKlarTilsending(skjema));
    }
}
