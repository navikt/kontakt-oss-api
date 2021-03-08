package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
@JsonDeserialize(using = NorgOrganisering.NorgOrganiseringDeserializer.class)
public class NorgOrganisering {
    private String enhetNr;
    private String status;
    private String overordnetEnhet;

    static class NorgOrganiseringDeserializer extends StdDeserializer<NorgOrganisering> {

        public NorgOrganiseringDeserializer() {
            super(NorgOrganisering.class);
        }

        @Override
        public NorgOrganisering deserialize(
                JsonParser parser,
                DeserializationContext deserializationContext
        ) throws IOException {
            JsonNode node = parser.getCodec().readTree(parser);
            return new NorgOrganisering(
                    node.get("enhet").get("enhetNr").textValue(),
                    node.get("enhet").get("status").textValue(),
                    node.get("overordnetEnhet").textValue()
            );
        }
    }
}
