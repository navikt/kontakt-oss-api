package no.nav.arbeidsgiver.kontakt.oss.salesforce.klient;

public class SalesforceException extends RuntimeException {
    public SalesforceException(String msg, Exception e) {
        super(msg, e);
    }

    public SalesforceException(String msg) {
        super(msg);
    }
}
