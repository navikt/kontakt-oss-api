package no.nav.tag.kontakt.oss.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.kontakt.oss.Kontaktskjema;
import no.nav.tag.kontakt.oss.events.BesvarelseMottatt;
import no.nav.tag.kontakt.oss.events.FylkesinndelingOppdatert;
import no.nav.tag.kontakt.oss.events.GsakOppgaveSendt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class MetricsListeners {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final MeterRegistry meterRegistry;

    private final static String KONTAKTSKJEMA_SUCCESS_COUNTER = "mottatt.kontaktskjema.success";
    private final static String KONTAKTSKJEMA_FAIL_COUNTER = "mottatt.kontaktskjema.fail";

    @Autowired
    public MetricsListeners(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // TODO: Fjern koden under når Kibana-boardet er oppe
        initierCountersMedFylkerOgTemaer();
    }

    private void initierCountersMedFylkerOgTemaer() {
        // TODO: Fjern koden under når Kibana-boardet er oppe

        List<String> fylker = Arrays.asList(
                "1000",
                "0400",
                "1500",
                "1800",
                "0300",
                "1100",
                "1900",
                "5700",
                "0800",
                "1200",
                "0600",
                "0200"
        );

        List<String> temaer = Arrays.asList(
                "Rekruttering",
                "Rekruttering med tilrettelegging",
                "Arbeidstrening",
                "Oppfølging av en arbeidstaker",
                "Annet"
        );

        List<String> counterNames = Arrays.asList(KONTAKTSKJEMA_SUCCESS_COUNTER, KONTAKTSKJEMA_FAIL_COUNTER);

        counterNames.forEach(counterName ->
                temaer.forEach(tema ->
                        fylker.forEach(fylke ->
                                Counter.builder(counterName)
                                        .tag("fylke", fylke)
                                        .tag("tema", tema)
                                        .register(meterRegistry)
                        )
                )
        );
    }

    @EventListener
    public void besvarelseMottatt(BesvarelseMottatt event) {
        Kontaktskjema kontaktskjema = event.getKontaktskjema();

        ObjectNode json = objectMapper.createObjectNode();

        json.put("event", "kontaktskjema.mottatt");
        json.put("success", event.isSuksess());
        json.put("fylke", kontaktskjema.getFylke());
        json.put("kommunenr", kontaktskjema.getKommunenr());
        json.put("kommune", kontaktskjema.getKommune());
        json.put("orgnr", kontaktskjema.getOrgnr());
        json.put("temaType", kontaktskjema.getTemaType().name());
        json.put("harSnakketMedAnsattrepresentant", kontaktskjema.getHarSnakketMedAnsattrepresentant());

        log.info(json.toString());

        // TODO: Fjern koden under når Kibana-boardet er oppe
        String counterName = event.isSuksess() ? KONTAKTSKJEMA_SUCCESS_COUNTER : KONTAKTSKJEMA_FAIL_COUNTER;

        Counter.builder(counterName)
                .tag("fylke", event.getKontaktskjema().getFylke())
                .tag("tema", event.getKontaktskjema().getTema())
                .register(meterRegistry)
                .increment();
    }

    @EventListener
    public void gsakOppgaveSendt(GsakOppgaveSendt event) {
        log.info("gsakoppgave sendt, success={}", event.isSuksess());

        // TODO: Fjern koden under når Kibana-boardet er oppe
        String counterName = event.isSuksess() ? "sendt.gsakoppgave.success" : "sendt.gsakoppgave.fail";

        Counter.builder(counterName)
                .register(meterRegistry)
                .increment();
    }

    @EventListener
    public void fylkesInndelingOppdatert(FylkesinndelingOppdatert event) {
        log.info("fylkesinndeling oppdatert, success={}", event.isSuksess());

        // TODO: Fjern koden under når Kibana-boardet er oppe
        String counterName = event.isSuksess() ? "hentet.fylkesinndeling.success" : "hentet.fylkesinndeling.fail";

        Counter.builder(counterName)
                .register(meterRegistry)
                .increment();
    }
}
