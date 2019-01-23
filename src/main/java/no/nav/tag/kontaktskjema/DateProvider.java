package no.nav.tag.kontaktskjema;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

@Component
public class DateProvider {

    public LocalDateTime now() {
        return LocalDateTime.now();
    }

}
