package no.nav.tag.kontakt.oss.vault;

import com.fasterxml.jackson.annotation.JsonProperty;

// TODO: Fjerne? Ikke brukt
public class AuthenticationDTO {
    public Auth auth;

    public static class Auth {
        @JsonProperty("client_token")
        public String clientToken;
    }
}
