package api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import data.Card;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

@Log
@NoArgsConstructor
public class APIGet {
    private static final String API_BASE_URL = "https://api.pokemontcg.io/v2/";
    private static final String CARD_URL = "https://api.pokemontcg.io/v2/cards/";
    private static final ObjectMapper objectMapper = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    private static String getSecret() {
        Dotenv dotenv = null;
        try {
            dotenv = Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load();
            return dotenv.get("API_KEY");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static Card getCardDataById(String cardId) {
        HttpClient client = HttpClient.newHttpClient();
        String apiKey = getSecret();
        try {
            URI targetURI = new URI(CARD_URL + cardId);
            HttpRequest request = HttpRequest.newBuilder().uri(targetURI).header("X-Api-Key", apiKey).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            if (statusCode == 200) {
                String jsonBody = response.body();
                try {
                    ResponseWrapper wrapper = objectMapper.readValue(jsonBody, ResponseWrapper.class);
                    return wrapper.getData();
                } catch (JsonProcessingException e) {
                    log.log(Level.SEVERE, "Error parsing JSON response: {0}", e.getMessage());
                    return null;
                }
            } else {
                log.log(Level.SEVERE, "Error: GET request did not return 200! It returned: {0}", statusCode);
                return null;
            }
        } catch (URISyntaxException e) {
            log.log(Level.SEVERE, "Error creating URI: {0}", e.getMessage());
            return null;
        } catch (IOException | InterruptedException e) {
            log.log(Level.SEVERE, "Error sending HTTP request: {0}", e.getMessage());
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return null;
        }
    }

    public static List<Card> queryCardsByName(String name) {
        String apiKey = getSecret();
        String uriPart = "name:" + name;
        String fullUriString = CARD_URL + "?q=" + uriPart;
        HttpClient client = HttpClient.newHttpClient();
        try {
            URI uri = new URI(fullUriString);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).header("X-Api-Key", apiKey).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            if (statusCode == 200) {
                String jsonBody = response.body();
                try {
                    ListResponseWrapper wrapper = objectMapper.readValue(jsonBody, ListResponseWrapper.class);
                    return wrapper.getData() != null ? wrapper.getData() : Collections.emptyList();
                } catch (JsonProcessingException e) {
                    log.log(Level.SEVERE, "Error parsing JSON response: {0}", e.getMessage());
                    return null;
                }
            } else {
                log.log(Level.SEVERE, "Error: Get request did not return 200! It returned: {0}", statusCode);
                return null;
            }
        } catch (URISyntaxException e) {
            log.log(Level.SEVERE, "Error in creating URI: {0}", e.getMessage());
            return null;
        } catch (IOException | InterruptedException e) {
            log.log(Level.SEVERE, "Error sending HTTP request: {0}", e.getMessage());
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return null;
        }
    }


}
