package no.nav.tag.kontaktskjema;

import io.prometheus.client.hotspot.DefaultExports;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KontaktskjemaApplication {

	public static void main(String[] args) {
		DefaultExports.initialize();
		SpringApplication.run(KontaktskjemaApplication.class, args);
	}
}
