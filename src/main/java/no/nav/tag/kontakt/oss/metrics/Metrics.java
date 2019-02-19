package no.nav.tag.kontakt.oss.metrics;

import io.micrometer.core.instrument.Counter;

public class Metrics {
    private final Counter mottattKontaktskjemaSuccess;
    private final Counter mottattKontaktskjemaFail;
    private final Counter sendtGsakOppgaveSuccess;
    private final Counter sendtGsakOppgaveFail;

    public Metrics(
            Counter mottattKontaktskjemaSuccess,
            Counter mottattKontaktskjemaFail,
            Counter sendtGsakOppgaveSuccess,
            Counter sendtGsakOppgaveFail
    ) {
        this.mottattKontaktskjemaSuccess = mottattKontaktskjemaSuccess;
        this.mottattKontaktskjemaFail = mottattKontaktskjemaFail;
        this.sendtGsakOppgaveSuccess = sendtGsakOppgaveSuccess;
        this.sendtGsakOppgaveFail = sendtGsakOppgaveFail;
    }

    public void mottattKontaktskjema(boolean success) {
        if (success) {
            this.mottattKontaktskjemaSuccess.increment();
        } else {
            this.mottattKontaktskjemaFail.increment();
        }
    }

    public void sendtGsakOppgave(boolean success) {
        if (success) {
            this.sendtGsakOppgaveSuccess.increment();
        } else {
            this.sendtGsakOppgaveFail.increment();
        }
    }
}
