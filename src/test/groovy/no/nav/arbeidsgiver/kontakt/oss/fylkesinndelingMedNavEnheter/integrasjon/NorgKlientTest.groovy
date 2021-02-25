package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon

import no.nav.arbeidsgiver.kontakt.oss.KontaktskjemaException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.core.io.Resource
import org.springframework.test.web.client.MockRestServiceServer
import spock.lang.Specification

import static org.springframework.http.HttpMethod.GET
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@RestClientTest(
        components = [NorgKlient, NorgOrganisering.NorgOrganiseringDeserializer],
        properties = [
                "norg.url=http://n.org",
        ]
)
@AutoConfigureWebClient//(registerRestTemplate = true)
class NorgKlientTest extends Specification {

    @Value("classpath:/norgOrganiseringReellRespons.json")
    Resource norgRespons

    @Value('${norg.url}')
    String norgUrl

    @Autowired
    MockRestServiceServer server

    @Autowired
    NorgKlient klient

    def "hentOrganiseringFraNorg skal oversette til riktig objekt"() {
        given:
        server
                .expect(requestTo(norgUrl + "/enhet/kontaktinformasjon/organisering/AKTIV"))
                .andExpect(method(GET))
                .andExpect(header("consumerId", "kontakt-oss-api"))
                .andRespond(withSuccess(norgRespons, APPLICATION_JSON))
        when:
        def result = klient.hentOrganiseringFraNorg()

        then:
        result == [
                new NorgOrganisering("9999", "Inaktiv", "1500"),
                new NorgOrganisering("1416", "Aktiv", "1400")
        ]
    }

    def "hentOrganiseringFraNorg skal feile hvis respons ikke returnerer ok"() {
        given:
        server
                .expect(requestTo(norgUrl + "/enhet/kontaktinformasjon/organisering/AKTIV"))
                .andExpect(method(GET))
                .andRespond(withStatus(INTERNAL_SERVER_ERROR))
        when:
        klient.hentOrganiseringFraNorg()

        then:
        thrown(KontaktskjemaException)
    }

    def "skal handtere at overordnetEnhet er null"() {
        given:
        def json = $/[{
            "enhet": { 
                "enhetNr": "4280", 
                "status": "Aktiv" 
            },
            "overordnetEnhet": null
        }]/$
        server
                .expect(requestTo(norgUrl + "/enhet/kontaktinformasjon/organisering/AKTIV"))
                .andExpect(method(GET))
                .andRespond(withSuccess(json, APPLICATION_JSON))
        when:
        def result = klient.hentOrganiseringFraNorg()

        then:
        result == [new NorgOrganisering("4280", "Aktiv", null)]
    }

    def "hentTilhoerendeNavEnhet skal returnere empty for 404"() {
        given:
        def kommunenrEllerBydelsnr = "42"
        server
                .expect(requestTo(norgUrl + "/enhet/navkontor/$kommunenrEllerBydelsnr"))
                .andExpect(method(GET))
                .andRespond(withStatus(NOT_FOUND))
        when:
        def result = klient.hentTilhoerendeNavEnhet(kommunenrEllerBydelsnr)

        then:
        result.isEmpty()
    }

    def "hentTilhoerendeNavEnhet skal returnere enhetsnummer"() {
        given:
        def kommunenrEllerBydelsnr = "42"
        def tilhoerendeEnhet = "4444"
        server
                .expect(requestTo(norgUrl + "/enhet/navkontor/$kommunenrEllerBydelsnr"))
                .andExpect(method(GET))
                .andRespond(withSuccess($/{"enhetNr": "$tilhoerendeEnhet"}/$, APPLICATION_JSON))
        when:
        def result = klient.hentTilhoerendeNavEnhet(kommunenrEllerBydelsnr)

        then:
        result.isPresent()
        result.get().enhetNr == tilhoerendeEnhet
    }
}