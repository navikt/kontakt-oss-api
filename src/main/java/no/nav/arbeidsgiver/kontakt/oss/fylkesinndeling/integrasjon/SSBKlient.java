package no.nav.arbeidsgiver.kontakt.oss.fylkesinndeling.integrasjon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class SSBKlient {
    private static final String CLASSIFICATION_ENDPOINT = "https://data.ssb.no/api/klass/v1/classifications/";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient client;

    public SSBKlient(){
        this.client = HttpClient.newHttpClient();
    }

    public List<ClassificationCode> getClassificationCodes(String classificationNumber) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CLASSIFICATION_ENDPOINT+classificationNumber+"/codes?from="+ LocalDate.now().toString()))
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() != 200){
            throw new ClassificationCodeException("Failed to retrieve classification codes from SSB"+"/n StatusCode: " +response.statusCode() +"/n message: " +response.body());
        }

        return getCodes(response.body());
    }

    private List<ClassificationCode> getCodes(String json) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(json);
        JsonNode codesNode = node.at("/codes");

        List<ClassificationCode> classificationCodes = Arrays.asList(objectMapper.treeToValue(codesNode, ClassificationCode[].class));

        return classificationCodes;
    }
}
