package no.nav.arbeidsgiver.kontakt.oss

import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.LokasjonsValidator
import no.nav.arbeidsgiver.kontakt.oss.kafka.utsending.KontaktskjemaUtsendingRepository;
import org.spockframework.spring.SpringBean
import org.spockframework.spring.StubBeans
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@StubBeans([
        KontaktskjemaUtsendingRepository,
        ApplicationEventPublisher,
        LokasjonsValidator,
])
@WebMvcTest(
        controllers = [KontaktskjemaController, KontaktskjemaService, DateProvider, KontaktskjemaValidator],
        properties = [
                "kontaktskjema.max-requests-per-10-min=200"
        ]
)
class KontaktskjemaControllerSpockTest extends Specification {

    @Autowired
    MockMvc mockMvc

    @SpringBean
    KontaktskjemaRepository kontaktskjemaRepository = Mock()

    def "skal sende skjema tross rare tegn i kommunenavn"() {
        given:
        _ * kontaktskjemaRepository.findAllNewerThan(_) >> []
        _ * kontaktskjemaRepository.toString()
        when:
        def result = mockMvc.perform(
                post("/meldInteresse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content($/{
                    "fylkesenhetsnr": "42",
                    "kommune": "Porsanger – Porsá?gu – Porsanki", 
                    "kommunenr": "42", 
                    "bedriftsnavn": "lol", 
                    "orgnr": "889640782", 
                    "fornavn": "lol", 
                    "etternavn": "lol", 
                    "epost": "lol@lol.no",
                    "telefonnr": "42",
                    "tema": "lol",
                    "temaType": "REKRUTTERING",
                    "harSnakketMedAnsattrepresentant": true 
                }/$)
        )

        then:
        1 * kontaktskjemaRepository.save(_) >> Stub(Kontaktskjema)
        result.andExpect(status().is2xxSuccessful())
    }
}
