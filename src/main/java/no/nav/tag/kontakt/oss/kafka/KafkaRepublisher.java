package no.nav.tag.kontakt.oss.kafka;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.KontaktskjemaRepository;
import no.nav.tag.kontakt.oss.gsak.GsakOppgave;
import no.nav.tag.kontakt.oss.gsak.GsakOppgaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
            GsakOppgaveRepository gsakOppgaveRepository, KontaktskjemaMottattProducer kontaktskjemaMottattProducer
    ) {
        this.kontaktskjemaRepository = kontaktskjemaRepository;
        this.gsakOppgaveRepository = gsakOppgaveRepository;
        this.kontaktskjemaMottattProducer = kontaktskjemaMottattProducer;
    }

    @GetMapping("/internal/kafka/{passord}")
    public String republishAlleKontaktskjemaer(
            @PathVariable String passord
    ) {
        if (!"hemmelig".equals(passord)) {
            return "dette skal du ikke gjøre";
        }
        Iterable<Kontaktskjema> kontaktskjemaer = kontaktskjemaRepository.findAll();
        Map<Integer, Integer> mapFraKontaktskjemaIdTilGsakId = mapFraKontaktskjemaIdTilGsakId();
        Integer antall = 1;

        String meldinger = "";

        for (Kontaktskjema kontaktskjema : kontaktskjemaer) {
            Integer gsakId = mapFraKontaktskjemaIdTilGsakId.get(kontaktskjema.getId());
            log.info("Publiserer kontaktskjema nr {} med id={}, gsakid={}", antall, kontaktskjema.getId(), gsakId);

            try {
                kontaktskjemaMottattProducer.kontaktskjemaMottatt(kontaktskjema, gsakId);
            } catch (Exception e) {
                log.error("Feilet publisering for kontaktskjema nr {} med id={}, gsakid={}", antall, kontaktskjema.getId(), gsakId, e);
            }

            meldinger += antall + ": kontaktskjemaId=" + kontaktskjema.getId() + ", gsakId=" + gsakId + "\n";

            antall++;
        }

        return meldinger;
    }

    private Map<Integer, Integer> mapFraKontaktskjemaIdTilGsakId() {
        Map<Integer, Integer> map = new HashMap<>();
        Iterable<GsakOppgave> gsakOppgaver = gsakOppgaveRepository.findAll();
        gsakOppgaver.forEach(gsakOppgave -> map.put(gsakOppgave.getKontaktskjemaId(), gsakOppgave.getGsakId()));
        return map;
    }
}
