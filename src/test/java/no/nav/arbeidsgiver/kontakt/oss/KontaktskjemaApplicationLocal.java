package no.nav.arbeidsgiver.kontakt.oss;

import org.springframework.boot.builder.SpringApplicationBuilder;

public class KontaktskjemaApplicationLocal extends KontaktskjemaApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(KontaktskjemaApplication.class)
                .profiles("local", "testdata")
                .build()
                .run();
    }
}
