package no.nav.arbeidsgiver.kontakt.oss.salesforce.utsending;

import lombok.extern.slf4j.Slf4j;
import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;
import no.nav.arbeidsgiver.kontakt.oss.kafka.KontaktskjemaKlarTilsending;
import no.nav.arbeidsgiver.kontakt.oss.salesforce.klient.SalesforceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
@Slf4j
public class KontaktskjemaUtsendingService {

    private final KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository;
    private final SalesforceService salesforceService;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public KontaktskjemaUtsendingService(
            KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository,
            SalesforceService salesforceService,
            ApplicationEventPublisher publisher
    ) {
        this.kontaktskjemaUtsendingRepository = kontaktskjemaUtsendingRepository;
        this.salesforceService = salesforceService;
        this.publisher = publisher;
    }

    @Transactional
    public void sendSkjemaTilSalesForce(Kontaktskjema skjema) {
        KontaktskjemaUtsending kontaktskjemaUtsending =
                kontaktskjemaUtsendingRepository.hentKontakskjemaUtsending(
                        skjema.getId()
                );
        kontaktskjemaUtsendingRepository.save(KontaktskjemaUtsending.sent(kontaktskjemaUtsending));
        publisher.publishEvent(new KontaktskjemaKlarTilsending(skjema));
        salesforceService.sendKontaktskjemaTilSalesforce(skjema);
    }
}
