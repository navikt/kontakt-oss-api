package no.nav.arbeidsgiver.kontakt.oss.kafka;

import lombok.extern.slf4j.Slf4j;
import no.nav.arbeidsgiver.kontakt.oss.Kontaktskjema;
import no.nav.arbeidsgiver.kontakt.oss.testUtils.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(properties = {"kontakt-oss.kafka.enabled=true", "kontakt-oss.kafka.test-instance.enabled=true", "mock.enabled=false"})
@DirtiesContext
@ActiveProfiles({"local"})
@EnableKafka
@EmbeddedKafka(
        partitions = 1, topics = {Topics.KONTAKTSKJEMA},
        brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@Slf4j
public class KontaktskjemaKafkaLytterTest {

    @Autowired
    private KontaktskjemaKafkaProducer kontaktskjemaKafkaProducer;

    @Autowired
    KontaktskjemaKafkaLytter kontaktskjemaKafkaLytter;

    @Test
    public void send_melding_pa_kafkatopic() throws InterruptedException {
        final Kontaktskjema kontaktskjema = TestData.kontaktskjema();
        kontaktskjema.setId(15);

        kontaktskjemaKafkaProducer.publiserMelding(kontaktskjema);
        kontaktskjemaKafkaLytter.getLatch().await(10000, TimeUnit.MILLISECONDS);

         // assertThat(kontaktskjemaKafkaLytter.getLatch().getCount()).isEqualTo(0L);


         assertThat(kontaktskjemaKafkaLytter.getPayload()).isNotEmpty();

    }

}
