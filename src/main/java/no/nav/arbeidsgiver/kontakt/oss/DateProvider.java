package no.nav.arbeidsgiver.kontakt.oss;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DateProvider {

    public LocalDateTime now() {
        return LocalDateTime.now();
    }

}
