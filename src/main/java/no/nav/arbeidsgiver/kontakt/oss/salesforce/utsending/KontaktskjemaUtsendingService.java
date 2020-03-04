package no.nav.arbeidsgiver.kontakt.oss.salesforce.utsending;

import lombok.extern.slf4j.Slf4j;
import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;
import no.nav.arbeidsgiver.kontakt.oss.salesforce.SalesforceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
@Slf4j
public class KontaktskjemaUtsendingService {

    private final KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository;
    private final SalesforceService salesforceService;

    @Autowired
    public KontaktskjemaUtsendingService(
            KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository,
            SalesforceService salesforceService) {
        this.kontaktskjemaUtsendingRepository = kontaktskjemaUtsendingRepository;
        this.salesforceService = salesforceService;
    }

    @Transactional
    public void sendSkjemaTilSalesForce(Kontaktskjema skjema) {
        KontaktskjemaUtsending kontaktskjemaUtsending =
                kontaktskjemaUtsendingRepository.hentKontakskjemaUtsending(
                        skjema.getId()
                );
        kontaktskjemaUtsendingRepository.save(KontaktskjemaUtsending.sent(kontaktskjemaUtsending));
        salesforceService.sendKontaktskjemaTilSalesforce(skjema);
    }
}
