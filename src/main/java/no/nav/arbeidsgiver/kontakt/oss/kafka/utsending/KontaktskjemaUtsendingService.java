package no.nav.arbeidsgiver.kontakt.oss.kafka.utsending;

import lombok.extern.slf4j.Slf4j;
import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;
import no.nav.arbeidsgiver.kontakt.oss.kafka.FormSubmission;
import no.nav.arbeidsgiver.kontakt.oss.kafka.KafkaMessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
@Slf4j
public class KontaktskjemaUtsendingService {

    private final KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository;
    private final KafkaMessageProducer kafkaProducer;

    @Autowired
    public KontaktskjemaUtsendingService(
            KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository, KafkaMessageProducer kafkaProducer) {
        this.kontaktskjemaUtsendingRepository = kontaktskjemaUtsendingRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Transactional
    public void publishFormSubmissionToKafka(Kontaktskjema skjema) {
        KontaktskjemaUtsending kontaktskjemaUtsending =
                kontaktskjemaUtsendingRepository.hentKontakskjemaUtsending(
                        skjema.getId()
                );
        kontaktskjemaUtsendingRepository.save(KontaktskjemaUtsending.sent(kontaktskjemaUtsending));

        FormSubmission submission = FormSubmission.builder()
                .organisationName(skjema.getBedriftsnavn())
                .municipalityCode(skjema.getKommunenr())
                .regionCode(skjema.getFylkesenhetsnr())
                .organisationNumber(skjema.getOrgnr())
                .phoneNo(skjema.getTelefonnr())
                .type(skjema.getTemaType())
                .email(skjema.getEpost())
                .name(skjema.getNavn())
                .Id(skjema.getId())
                .build();
        kafkaProducer.publish(submission.getId().toString(), submission.toString());

    }
}
