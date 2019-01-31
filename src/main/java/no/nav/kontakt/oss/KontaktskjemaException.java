package no.nav.kontakt.oss;

public class KontaktskjemaException extends RuntimeException {
    public KontaktskjemaException(String message, Exception e) {
        super(message, e);
    }

    public KontaktskjemaException(String message) {
        super(message);
    }
}
