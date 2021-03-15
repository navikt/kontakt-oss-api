package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon

import no.nav.arbeidsgiver.kontakt.oss.KontaktskjemaException
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.Kommune
import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.KommuneEllerBydel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.core.io.Resource
import org.springframework.http.HttpMethod
import org.springframework.test.web.client.MockRestServiceServer
import spock.lang.Specification

import static no.nav.arbeidsgiver.kontakt.oss.testUtils.TestData.lesFil
import static org.hamcrest.core.IsNull.notNullValue
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@RestClientTest(
        components = [KodeverkKlient],
        properties = [
                "kodeverk.url=http://kodeve.rk",
        ]
)
@AutoConfigureWebClient
//(registerRestTemplate = true)
class KodeverkKlientTest extends Specification {

    @Value("classpath:/mock/kommuner.json")
    Resource kommunerRespons

    @Value("classpath:/mock/bydeler.json")
    Resource bydelerRespons

    @Value('${kodeverk.url}')
    String kodeverkUrl

    @Autowired
    MockRestServiceServer server

    @Autowired
    KodeverkKlient klient

    def "hentKommuner skal lese produksjonsrespons uten feil"() {
        given:
        def kodeverksnavn = "Kommuner"
        def requestUrl = kodeverkUrl + "/kodeverk/$kodeverksnavn/koder/betydninger?ekskluderUgyldige=true&spraak=nb"
        server
                .expect(requestTo(requestUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Nav-Consumer-Id", "kontakt-oss-api"))
                .andExpect(header("Nav-Call-Id", notNullValue()))
                .andRespond(withSuccess(kommunerRespons, APPLICATION_JSON))
        when:
        def result = klient.hentKommuner()

        then:
        result.size() > 400
        result.contains( new Kommune("0101", "Halden") )
    }

    def "hentKommuner skal hente kommuner fra json"() {
        given:
        def respons = lesFil("kommuner.json")
        server
                .expect(requestTo(kodeverkUrl + "/kodeverk/Kommuner/koder/betydninger?ekskluderUgyldige=true&spraak=nb"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(respons, APPLICATION_JSON))
        when:
        def result = klient.hentKommuner()

        then:
        result == [
                new Kommune("1001", "Kristiansand"),
                new Kommune("1002", "Mandal")
        ]
    }

    def "hentKommuner skal ikke ta med kommuner uten beskrivelse"() {
        given:
        def respons = $/{ "betydninger": { "1001": [] } }/$
        server
                .expect(requestTo(kodeverkUrl + "/kodeverk/Kommuner/koder/betydninger?ekskluderUgyldige=true&spraak=nb"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(respons, APPLICATION_JSON))
        when:
        def result = klient.hentKommuner()

        then:
        result.isEmpty()
    }

    def "hentKommuner skal feile hvis respons ikke returnerer gyldig json"() {
        given:
        def respons = 'ikke gyldig json'
        server
                .expect(requestTo(kodeverkUrl + "/kodeverk/Kommuner/koder/betydninger?ekskluderUgyldige=true&spraak=nb"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(respons, APPLICATION_JSON))
        when:
        klient.hentKommuner()

        then:
        thrown(KontaktskjemaException)
    }

    def "hentBydeler skal lese produksjonsrespons uten feil"() {
        given:
        server
                .expect(requestTo(kodeverkUrl + "/kodeverk/Bydeler/koder/betydninger?ekskluderUgyldige=true&spraak=nb"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Nav-Consumer-Id", "kontakt-oss-api"))
                .andExpect(header("Nav-Call-Id", notNullValue()))
                .andRespond(withSuccess(bydelerRespons, APPLICATION_JSON))
        when:
        def result = klient.hentBydeler()

        then:
        result.size() > 30
    }

    def "hentBydeler skal hente kommuner fra json"() {
        given:
        server
                .expect(requestTo(kodeverkUrl + "/kodeverk/Bydeler/koder/betydninger?ekskluderUgyldige=true&spraak=nb"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Nav-Consumer-Id", "kontakt-oss-api"))
                .andExpect(header("Nav-Call-Id", notNullValue()))
                .andRespond(withSuccess(lesFil("bydeler.json"), APPLICATION_JSON))
        when:
        def result = klient.hentBydeler()

        then:
        result == [
                new KommuneEllerBydel("110301", "Hundvåg"),
                new KommuneEllerBydel("110302", "Tasta")
        ]
    }
    def "hentBydeler skal ikke ta med kommuner uten beskrivelse"() {
        given:
        def respons = $/{ "betydninger": { "1001": [] } }/$
        server
                .expect(requestTo(kodeverkUrl + "/kodeverk/Bydeler/koder/betydninger?ekskluderUgyldige=true&spraak=nb"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(respons, APPLICATION_JSON))
        when:
        def result = klient.hentBydeler()

        then:
        result.isEmpty()
    }

    def "hentBydeler skal feile hvis respons ikke returnerer gyldig json"() {
        given:
        def respons = 'ikke gyldig json'
        server
                .expect(requestTo(kodeverkUrl + "/kodeverk/Bydeler/koder/betydninger?ekskluderUgyldige=true&spraak=nb"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(respons, APPLICATION_JSON))
        when:
        klient.hentBydeler()

        then:
        thrown(KontaktskjemaException)
    }
}