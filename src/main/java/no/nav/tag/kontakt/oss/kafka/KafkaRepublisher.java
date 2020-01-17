package no.nav.tag.kontakt.oss.kafka;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.KontaktskjemaRepository;
import no.nav.tag.kontakt.oss.gsak.GsakOppgave;
import no.nav.tag.kontakt.oss.gsak.GsakOppgaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Component
@Slf4j
public class KafkaRepublisher {

    private final KontaktskjemaRepository kontaktskjemaRepository;
    private final GsakOppgaveRepository gsakOppgaveRepository;
    private final KontaktskjemaMottattProducer kontaktskjemaMottattProducer;

    @Autowired
    public KafkaRepublisher(
            KontaktskjemaRepository kontaktskjemaRepository,
            GsakOppgaveRepository gsakOppgaveRepository,
            KontaktskjemaMottattProducer kontaktskjemaMottattProducer
    ) {
        this.kontaktskjemaRepository = kontaktskjemaRepository;
        this.gsakOppgaveRepository = gsakOppgaveRepository;
        this.kontaktskjemaMottattProducer = kontaktskjemaMottattProducer;
    }

    @PostMapping("/internal/kafka/republish")
    public String republishAlleKontaktskjemaer() {
        // Endepunktet publiserer alle kontaktskjemaene fra databasen til Kafka-topicet. Brukes bare i spesielle situasjoner.

        Iterable<Kontaktskjema> kontaktskjemaer = kontaktskjemaRepository.findAll();
        Map<Integer, Integer> mapFraKontaktskjemaIdTilGsakId = mapFraKontaktskjemaIdTilGsakId();

        int antallSkjemaer = 0;
        int antallFeil = 0;
        String melding = "";

        for (Kontaktskjema kontaktskjema : kontaktskjemaer) {
            antallSkjemaer++;
            Integer gsakId = mapFraKontaktskjemaIdTilGsakId.get(kontaktskjema.getId());
            log.info("Publiserer kontaktskjema nr {} med id={}, gsakid={}", antallSkjemaer, kontaktskjema.getId(), gsakId);

            try {
                kontaktskjemaMottattProducer.kontaktskjemaMottatt(kontaktskjema, gsakId);
            } catch (Exception e) {
                log.error("Feilet publisering for kontaktskjema nr {} med id={}, gsakId={}", antallSkjemaer, kontaktskjema.getId(), gsakId, e);
                antallFeil += 1;
            }

            melding += antallSkjemaer + ": kontaktskjemaId=" + kontaktskjema.getId() + ", gsakId=" + gsakId + " --- ";
        }

        return "Antall feil: " + antallFeil + " Antall kontaktskjemaer: " + antallSkjemaer + " --- " + melding;
    }

    private Map<Integer, Integer> mapFraKontaktskjemaIdTilGsakId() {
        Map<Integer, Integer> map = new HashMap<>();
        Iterable<GsakOppgave> gsakOppgaver = gsakOppgaveRepository.findAll();
        gsakOppgaver.forEach(gsakOppgave -> map.put(gsakOppgave.getKontaktskjemaId(), gsakOppgave.getGsakId()));
        return map;
    }
}
