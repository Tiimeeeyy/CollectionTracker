package util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Utility class for caching and asynchronously loading images.
 */
public class ImageCache {
    // Thread pool for loading images in background
    private static final ExecutorService imageLoaderExecutor = Executors.newFixedThreadPool(3);
    
    // Cache for storing loaded images
    private static final Map<String, Image> imageCache = new ConcurrentHashMap<>();
    
    // Placeholder image for when an image is loading
    private static final Image LOADING_PLACEHOLDER = createPlaceholderImage("Loading...");
    
    // Placeholder image for when an image fails to load
    private static final Image ERROR_PLACEHOLDER = createPlaceholderImage("Error");
    
    /**
     * Asynchronously loads an image from a URL and caches it.
     * Returns a placeholder image immediately and updates the component when the image is loaded.
     * 
     * @param url The URL of the image to load
     * @param targetWidth The target width for scaling the image (-1 for maintaining aspect ratio)
     * @param targetHeight The target height for scaling the image
     * @param imageConsumer A consumer that will be called with the loaded image
     * @return A placeholder image that will be displayed while the actual image is loading
     */
    public static Image loadImageAsync(String url, int targetWidth, int targetHeight, 
                                      ImageConsumer imageConsumer) {
        if (url == null || url.isEmpty()) {
            return ERROR_PLACEHOLDER;
        }
        
        // Check if the image is already in the cache
        String cacheKey = url + "_" + targetWidth + "_" + targetHeight;
        Image cachedImage = imageCache.get(cacheKey);
        if (cachedImage != null) {
            // If the image is already cached, return it immediately
            imageConsumer.imageLoaded(cachedImage);
            return cachedImage;
        }
        
        // If not in cache, load it asynchronously
        imageLoaderExecutor.submit(() -> {
            try {
                // Load the image from URL
                URL imageUrl = new URL(url);
                BufferedImage originalImage = ImageIO.read(imageUrl);
                
                if (originalImage != null) {
                    // Scale the image
                    Image scaledImage = scaleImage(originalImage, targetWidth, targetHeight);
                    
                    // Cache the scaled image
                    imageCache.put(cacheKey, scaledImage);
                    
                    // Notify the consumer on the EDT
                    SwingUtilities.invokeLater(() -> imageConsumer.imageLoaded(scaledImage));
                } else {
                    SwingUtilities.invokeLater(() -> imageConsumer.imageLoaded(ERROR_PLACEHOLDER));
                }
            } catch (IOException e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> imageConsumer.imageLoaded(ERROR_PLACEHOLDER));
            }
        });
        
        // Return the loading placeholder immediately
        return LOADING_PLACEHOLDER;
    }
    
    /**
     * Scales an image to the specified dimensions.
     */
    private static Image scaleImage(Image image, int width, int height) {
        return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
    
    /**
     * Creates a simple placeholder image with text.
     */
    private static Image createPlaceholderImage(String text) {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, 100, 100);
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, 20, 50);
        g2d.dispose();
        return image;
    }
    
    /**
     * Clears the image cache.
     */
    public static void clearCache() {
        imageCache.clear();
    }
    
    /**
     * Interface for consuming loaded images.
     */
    public interface ImageConsumer {
        void imageLoaded(Image image);
    }
}