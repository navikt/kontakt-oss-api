package no.nav.arbeidsgiver.kontakt.oss.kafka.utsending;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public class KontaktskjemaUtsending {

    @Id
    private Integer id;
    private Integer kontaktskjemaId;
    private UtsendingStatus utsendingStatus;
    private LocalDateTime opprettet;

    public static KontaktskjemaUtsending klarTilUtsending(Integer kontaktskjemaId, LocalDateTime opprettet) {
        return nyKontaktskjemaUtsending(kontaktskjemaId, opprettet, UtsendingStatus.KLAR);
    }

    public static KontaktskjemaUtsending sent(KontaktskjemaUtsending lagretKontaktskjemaUtsending) {
        KontaktskjemaUtsending kontaktskjemaUtsending = nyKontaktskjemaUtsending(
                lagretKontaktskjemaUtsending.kontaktskjemaId,
                lagretKontaktskjemaUtsending.opprettet,
                UtsendingStatus.SENT
        );
        kontaktskjemaUtsending.id = lagretKontaktskjemaUtsending.id;
        return kontaktskjemaUtsending;
    }

    public static KontaktskjemaUtsending nyKontaktskjemaUtsending(
            Integer kontaktskjemaId,
            LocalDateTime opprettet,
            UtsendingStatus utsendingStatus
    ) {
        KontaktskjemaUtsending kontaktskjemaUtsending = new KontaktskjemaUtsending();
        kontaktskjemaUtsending.kontaktskjemaId = kontaktskjemaId;
        kontaktskjemaUtsending.opprettet = opprettet;
        kontaktskjemaUtsending.utsendingStatus = utsendingStatus;
        return kontaktskjemaUtsending;
    }

    public enum UtsendingStatus {
        KLAR,
        SENT
    }

    public boolean erSent() {
        return UtsendingStatus.SENT.equals(utsendingStatus);
    }
}
