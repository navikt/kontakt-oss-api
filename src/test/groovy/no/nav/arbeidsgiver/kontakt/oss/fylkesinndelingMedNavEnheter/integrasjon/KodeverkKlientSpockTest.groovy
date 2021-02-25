package no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.integrasjon

import no.nav.arbeidsgiver.kontakt.oss.fylkesinndelingMedNavEnheter.Kommune
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.core.io.Resource
import org.springframework.http.HttpMethod
import org.springframework.test.web.client.MockRestServiceServer
import spock.lang.Specification

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
@AutoConfigureWebClient//(registerRestTemplate = true)
class KodeverkKlientSpockTest extends Specification {

    @Value("classpath:/mock/kommuner.json")
    Resource kommunerRespons

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
        result.first() == new Kommune("0101", "Halden")
    }
}