package util;

import data.Card;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for caching API responses to reduce network requests.
 */
public class APICache {
    // Cache for storing card data by ID
    private static final Map<String, CacheEntry<Card>> cardCache = new ConcurrentHashMap<>();
    
    // Cache for storing search results by query
    private static final Map<String, CacheEntry<List<Card>>> searchCache = new ConcurrentHashMap<>();
    
    // Cache expiration time in milliseconds (10 minutes)
    private static final long CACHE_EXPIRATION = TimeUnit.MINUTES.toMillis(10);
    
    /**
     * Gets a card from the cache by ID.
     * 
     * @param cardId The ID of the card to retrieve
     * @return The cached card, or null if not in cache or expired
     */
    public static Card getCardById(String cardId) {
        CacheEntry<Card> entry = cardCache.get(cardId);
        if (entry != null && !entry.isExpired()) {
            return entry.getData();
        }
        return null;
    }
    
    /**
     * Puts a card into the cache.
     * 
     * @param cardId The ID of the card
     * @param card The card data to cache
     */
    public static void putCard(String cardId, Card card) {
        if (card != null) {
            cardCache.put(cardId, new CacheEntry<>(card));
        }
    }
    
    /**
     * Gets search results from the cache by query.
     * 
     * @param query The search query
     * @return The cached search results, or null if not in cache or expired
     */
    public static List<Card> getSearchResults(String query) {
        CacheEntry<List<Card>> entry = searchCache.get(query);
        if (entry != null && !entry.isExpired()) {
            return entry.getData();
        }
        return null;
    }
    
    /**
     * Puts search results into the cache.
     * 
     * @param query The search query
     * @param results The search results to cache
     */
    public static void putSearchResults(String query, List<Card> results) {
        if (results != null) {
            searchCache.put(query, new CacheEntry<>(results));
        }
    }
    
    /**
     * Clears all caches.
     */
    public static void clearCache() {
        cardCache.clear();
        searchCache.clear();
    }
    
    /**
     * A cache entry with expiration time.
     */
    private static class CacheEntry<T> {
        private final T data;
        private final long timestamp;
        
        public CacheEntry(T data) {
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }
        
        public T getData() {
            return data;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_EXPIRATION;
        }
    }
}