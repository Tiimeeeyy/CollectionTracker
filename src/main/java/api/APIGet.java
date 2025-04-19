package api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import data.Card;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import util.APICache;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Log
@NoArgsConstructor
public class APIGet {
    private static final String API_BASE_URL = "https://api.pokemontcg.io/v2/";
    private static final String CARD_URL = "https://api.pokemontcg.io/v2/cards/";
    private static final ObjectMapper objectMapper = createObjectMapper();
    private static final String apiKey = getSecret();
    private static final HttpClient client = HttpClient.newHttpClient();

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


    public static CompletableFuture<Card> getCardDataById(String cardId) {
        // Check if the card is in the cache
        Card cachedCard = APICache.getCardById(cardId);
        if (cachedCard != null) {
            log.info("Card found in cache: " + cardId);
            CompletableFuture<Card> future = new CompletableFuture<>();
            future.complete(cachedCard);
            return future;
        }

        try {
            URI targetURI = new URI(CARD_URL + cardId);
            HttpRequest request = HttpRequest.newBuilder().uri(targetURI).header("X-Api-Key", apiKey).GET().build();
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
                if (response.statusCode() == 200) {
                    try {
                        ResponseWrapper wrapper = objectMapper.readValue(response.body(), ResponseWrapper.class);
                        Card card = wrapper.getData();

                        // Cache the card data
                        if (card != null) {
                            APICache.putCard(cardId, card);
                        }

                        return card;
                    } catch (JsonProcessingException e) {
                        log.log(Level.SEVERE, "Error parsing JSON response: {0}", e.getMessage());
                        return null;
                    }
                } else {
                    log.log(Level.SEVERE, "Error! GET request returned {0} instead of 200!", response.statusCode());
                    return null;
                }
            }).exceptionally(e -> {
                log.log(Level.SEVERE, "Error sending HTTP request: {0}!", e.getMessage());
                return null;
            });
        } catch (URISyntaxException e) {
            log.log(Level.SEVERE, "Error creating URI: {0}", e.getMessage());
            CompletableFuture<Card> future = new CompletableFuture<>();
            future.complete(null);
            return future;
        }
    }

    public static CompletableFuture<List<Card>> queryCardsByName(String name) {
        // Check if the search results are in the cache
        List<Card> cachedResults = APICache.getSearchResults(name);
        if (cachedResults != null) {
            log.info("Search results found in cache for: " + name);
            CompletableFuture<List<Card>> future = new CompletableFuture<>();
            future.complete(cachedResults);
            return future;
        }

        String uriPart = "name:" + name;
        String fullUriString = CARD_URL + "?q=" + uriPart;
        try {
            URI uri = new URI(fullUriString);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).header("X-Api-Key", apiKey).GET().build();
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
                if (response.statusCode() == 200) {
                    try {
                        String responseBody = response.body();
                        double sizeInMB = (double) responseBody.getBytes().length / (1024 * 1024);
                        log.info(String.format("Response for query '%s': %.2f MB", name, sizeInMB));
                        ListResponseWrapper wrapper = objectMapper.readValue(response.body(), ListResponseWrapper.class);
                        List<Card> results = wrapper.getData();

                        // Cache the search results
                        if (results != null) {
                            APICache.putSearchResults(name, results);
                        }

                        log.info("Request processed");
                        return results;
                    } catch (JsonProcessingException e) {
                        log.log(Level.SEVERE, "Error parsing JSON response: {0}", e.getMessage());
                        return null;
                    }
                } else {
                    log.log(Level.SEVERE, "Error! GET requested returned {0} instead of 200!", response.statusCode());
                    return null;
                }
            }).exceptionally(e -> {
                log.log(Level.SEVERE, "Error sending HTTP request: {0}", e.getMessage());
                return null;
            });
        } catch (URISyntaxException e) {
            log.log(Level.SEVERE, "Error in creating URI: {0}", e.getMessage());
            CompletableFuture<List<Card>> future = new CompletableFuture<>();
            future.complete(null);
            return future;
        }
    }
}
