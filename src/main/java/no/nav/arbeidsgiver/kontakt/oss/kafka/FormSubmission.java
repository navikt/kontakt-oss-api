package no.nav.arbeidsgiver.kontakt.oss.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import no.nav.arbeidsgiver.kontakt.oss.TemaType;

@Builder
@Data
@AllArgsConstructor
public class FormSubmission {
    private final String organisationNumber;
    private final String organisationName;
    private final String municipalityCode;
    private final String regionCode;
    private final String phoneNo;
    private final String email;
    private final String name;

    private final Integer Id;
    private final TemaType type;

}