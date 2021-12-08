package no.nav.arbeidsgiver.kontakt.oss.kafka.utsending;

import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;
import no.nav.arbeidsgiver.kontakt.oss.KontaktskjemaRepository;
import no.nav.arbeidsgiver.kontakt.oss.kafka.FormSubmission;
import no.nav.arbeidsgiver.kontakt.oss.kafka.KafkaMessageProducer;
import no.nav.arbeidsgiver.kontakt.oss.kafka.utsending.KontaktskjemaUtsending;
import no.nav.arbeidsgiver.kontakt.oss.kafka.utsending.KontaktskjemaUtsendingRepository;
import no.nav.arbeidsgiver.kontakt.oss.kafka.utsending.KontaktskjemaUtsendingService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.time.LocalDateTime.now;
import static no.nav.arbeidsgiver.kontakt.oss.testUtils.TestData.kontaktskjemaBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@DirtiesContext
@ActiveProfiles({"kafka-test", "local"})
@TestPropertySource(properties = {"mock.enabled=false"})
@EmbeddedKafka(
        controlledShutdown = true,
        topics = {"arbeidsgiver.arbeidsgiver-kontaktskjema"},
        partitions = 1
)
public class KontaktskjemaUtsendingServiceTest {

    private static String TOPIC_NAME = "arbeidsgiver-kontaktskjema";
    private BlockingQueue<ConsumerRecord<String, String>> consumerRecords;
    private KafkaMessageListenerContainer<String, String> container;

    @Autowired
    private KontaktskjemaUtsendingRepository kontaktskjemaUtsendingRepository;
    @Autowired
    private KontaktskjemaUtsendingService kontaktskjemaUtsendingService;
    @Autowired
    private KontaktskjemaRepository kontaktskjemaRepository;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;


    @BeforeEach
    public void setUp() {

        cleanUpDb();

        consumerRecords = new LinkedBlockingQueue<>();

        ContainerProperties containerProperties = new ContainerProperties(TOPIC_NAME);
        Map<String, Object> consumerProperties =
                KafkaTestUtils.consumerProps(
                        "consumer",
                        "false",
                        embeddedKafkaBroker
                );
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "60000");
        DefaultKafkaConsumerFactory<String, String> kafkaConsumerFactory =
                new DefaultKafkaConsumerFactory<>(consumerProperties);

        container = new KafkaMessageListenerContainer<>(kafkaConsumerFactory, containerProperties);
        container.setupMessageListener((MessageListener<String, String>) record -> {
            consumerRecords.add(record);
        });

        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());

    }

    @AfterEach
    public void tearDown() {
        container.stop();
        cleanUpDb();
    }

    @Test
    public void publishToKafkaSuccess_StatusUpdatedToSent() {
        Kontaktskjema kontaktskjema = opprettOgHentKontaktskjema();
        kontaktskjemaUtsendingRepository.save(KontaktskjemaUtsending.klarTilUtsending(kontaktskjema.getId(), now()));
        sjekkSkjemaErLagretOgKlartTilUtsending(kontaktskjema.getId());

        kontaktskjemaUtsendingService.publishFormSubmissionToKafka(kontaktskjema);

        KontaktskjemaUtsending kontaktskjemaUtsending = kontaktskjemaUtsendingRepository.findAll().iterator().next();
        assertThat(kontaktskjemaUtsending.erSent()).isTrue();
    }



    @Test
    public void publishToKafkaError_StatusNotUpdatedToSent() {

        KontaktskjemaUtsendingService service = Mockito.mock(KontaktskjemaUtsendingService.class);
        Mockito.doThrow(new TimeoutException())
                .when(service)
                .publishFormSubmissionToKafka(Mockito.any(Kontaktskjema.class));

        Kontaktskjema kontaktskjema = opprettOgHentKontaktskjema();
        kontaktskjemaUtsendingRepository.save(KontaktskjemaUtsending.klarTilUtsending(kontaktskjema.getId(), now()));
        sjekkSkjemaErLagretOgKlartTilUtsending(kontaktskjema.getId());


        try {
            assertThrows(
                    TimeoutException.class,
                    () ->  service.publishFormSubmissionToKafka(kontaktskjema)
            );
        } catch (TimeoutException e) {

            KontaktskjemaUtsending kontaktskjemaUtsending = kontaktskjemaUtsendingRepository.findAll().iterator().next();
            assertThat(kontaktskjemaUtsending.erSent()).isFalse();
        }

    }


    @Test
    public void publishToKafkaSuccess_MessagePublishedToTopic() throws Exception{

        Kontaktskjema kontaktskjema = opprettOgHentKontaktskjema();
        kontaktskjemaUtsendingRepository.save(KontaktskjemaUtsending.klarTilUtsending(kontaktskjema.getId(), now()));
        sjekkSkjemaErLagretOgKlartTilUtsending(kontaktskjema.getId());

        kontaktskjemaUtsendingService.publishFormSubmissionToKafka(kontaktskjema);

        FormSubmission submission = FormSubmission.builder()
                .Id(kontaktskjema.getId())
                .email(kontaktskjema.getEpost())
                .organisationNumber(kontaktskjema.getOrgnr())
                .name(kontaktskjema.getNavn())
                .organisationName(kontaktskjema.getBedriftsnavn())
                .municipalityCode(kontaktskjema.getKommunenr())
                .type(kontaktskjema.getTemaType())
                .regionCode(kontaktskjema.getFylkesenhetsnr())
                .phoneNo(kontaktskjema.getTelefonnr())
                .build();

        ConsumerRecord<String, String> received = consumerRecords.poll(10, TimeUnit.SECONDS);

        Assert.assertEquals(submission.toString(), received.value());
    }


    private void sjekkSkjemaErLagretOgKlartTilUtsending(Integer kontaktskjemaId) {
        KontaktskjemaUtsending kontaktskjemaUtsendingBeforeSending = kontaktskjemaUtsendingRepository.hentKontakskjemaUtsending(kontaktskjemaId);
        assertThat(kontaktskjemaUtsendingBeforeSending.erSent()).isFalse();
    }

    private Kontaktskjema opprettOgHentKontaktskjema() {
        kontaktskjemaRepository.save(kontaktskjemaBuilder().build());
        Iterable<Kontaktskjema> alleKontaktskjemaer = kontaktskjemaRepository.findAll();
        return alleKontaktskjemaer.iterator().next();
    }

    private void cleanUpDb() {
        kontaktskjemaRepository.deleteAll();
        kontaktskjemaUtsendingRepository.deleteAll();
    }
}
