package no.nav.tag.kontakt.oss.salesforce;

import lombok.AllArgsConstructor;
import lombok.Data;
import no.nav.tag.kontakt.oss.TemaType;

@Data
@AllArgsConstructor
public class ContactForm {
    private final TemaType type;
    private final String municipalityCode;
    private final String organisationName;
    private final String organisationNumber;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phoneNo;
}
