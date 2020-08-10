package no.nav.arbeidsgiver.kontakt.oss.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("API for kontaktskjema for arbeidsgivere"))
                .externalDocs(new ExternalDocumentation()
                        .description("Repo på github")
                        .url("https://github.com/navikt/kontakt-oss-api"));
    }
}
