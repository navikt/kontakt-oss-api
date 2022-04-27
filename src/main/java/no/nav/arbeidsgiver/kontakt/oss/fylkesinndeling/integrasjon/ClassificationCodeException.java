package no.nav.arbeidsgiver.kontakt.oss.fylkesinndeling.integrasjon;

public class ClassificationCodeException extends RuntimeException{
    public ClassificationCodeException(String message, Exception e) {
        super(message, e);
    }

    public ClassificationCodeException(String message) {
        super(message);
    }
}
