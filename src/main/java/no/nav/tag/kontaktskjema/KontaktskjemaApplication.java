package no.nav.tag.kontaktskjema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = { EnheterConfiguration.class})
public class KontaktskjemaApplication {

	public static void main(String[] args) {
		SpringApplication.run(KontaktskjemaApplication.class, args);
	}
}
