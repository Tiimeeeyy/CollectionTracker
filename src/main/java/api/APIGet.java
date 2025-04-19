package api;

import data.Card;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import util.APICache;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Log
@NoArgsConstructor
public class APIGet {
    private static final String API_BASE_URL = "https://api.pokemontcg.io/v2/";
    /* unused */
    private static final String CARD_URL = "https://api.pokemontcg.io/v2/cards/";
    private static final String CARD_ENDPOINT = "/cards/{cardId}";
    private static final String apiKey = getSecret();
    private static final WebClient client = WebClient.builder()
            .baseUrl(API_BASE_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("X-Api-Key", apiKey)
            .build();

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
        Card cachedCard = APICache.getCardById(cardId);
        if (cachedCard != null) {
            return CompletableFuture.completedFuture(cachedCard);
        }
        Mono<Card> cardMono = client.get().uri(CARD_ENDPOINT, cardId).retrieve()
                .bodyToMono(ResponseWrapper.class).map(ResponseWrapper::getData)
                .doOnSuccess(card -> {
                    if (card != null) {
                        APICache.putCard(cardId, card);
                        log.info("Successfully fetched card with ID: " + cardId);
                    } else {
                        log.warning("Fetched card data was null for ID: " + cardId);
                    }
                }).doOnError(error -> log.log(Level.SEVERE, "Error fetching card data: {0}", error.getMessage()))
                .onErrorResume(error -> {
                    log.log(Level.SEVERE, "API call failed: {0}!", error.getMessage());
                    return Mono.empty();
                });
        return cardMono.toFuture();
    }

    public static CompletableFuture<List<Card>> queryCardsByName(String name) {
        List<Card> cachedResults = APICache.getSearchResults(name);
        if (cachedResults != null) {
            return CompletableFuture.completedFuture(cachedResults);
        }
        String queryParam = "name:" + name;
        Mono<List<Card>> cardMono = client.get().uri(uriBuilder -> uriBuilder.path("/cards")
                        .queryParam("q", queryParam).build()).retrieve()
                .bodyToMono(ListResponseWrapper.class).map(ListResponseWrapper::getData)
                .doOnSuccess(results -> {
                    if (results != null) {
                        APICache.putSearchResults(name, results);
                        log.info(String.format("Successfully fetched %s cards for query '%s'", results.size(), name));
                    } else {
                        log.warning("Fetched card list was null for query: " + name);
                    }
                }).doOnError(error -> log.log(Level.SEVERE, "Error querying for cards: {0}", error.getMessage()))
                .onErrorResume(e -> {
                    log.log(Level.SEVERE, "API call failed for query: {0}", e.getMessage());
                    return Mono.empty();
                });
        return cardMono.toFuture();
    }

    public static CompletableFuture<List<Card>> queryCardsBySet(String set) {
        List<Card> cachedResults = APICache.getSearchResults(set);
        if (cachedResults != null) {
            return CompletableFuture.completedFuture(cachedResults);
        }
        String queryString = "set.id:" + set;
        Mono<List<Card>> cardMono = client.get().uri(uriBuilder -> uriBuilder.path("/cards")
                        .queryParam("q", queryString).build()).retrieve()
                .bodyToMono(ListResponseWrapper.class).map(ListResponseWrapper::getData)
                .doOnSuccess(results -> {
                    if (results != null) {
                        APICache.putSearchResults(set, results);
                        log.info(String.format("Successfully fetched %s cards or query '%s'", results.size(), set));
                    } else {
                        log.warning("Fetched card list was null for query: " + set);
                    }
                }).doOnError(e -> log.log(Level.SEVERE, "Error querying for cards: {0}", e.getMessage()))
                .onErrorResume(e -> {
                    log.log(Level.SEVERE, "API call failed for query: {0}", e.getMessage());
                    return Mono.empty();
                });
        return cardMono.toFuture();
    }
}

