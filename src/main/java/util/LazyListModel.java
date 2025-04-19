package util;

import data.Card;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A list model that supports lazy loading of items.
 * This helps improve performance when displaying large lists by only creating
 * UI components for visible items.
 */
public class LazyListModel extends AbstractListModel<JPanel> {
    private final List<Card> cards;
    private final Function<Card, JPanel> panelCreator;
    private final List<JPanel> cachedPanels;
    private final int batchSize;
    
    /**
     * Creates a new LazyListModel.
     * 
     * @param cards The list of cards to display
     * @param panelCreator A function that creates a panel for a card
     * @param batchSize The number of items to load at once
     */
    public LazyListModel(List<Card> cards, Function<Card, JPanel> panelCreator, int batchSize) {
        this.cards = cards;
        this.panelCreator = panelCreator;
        this.batchSize = batchSize;
        this.cachedPanels = new ArrayList<>(cards.size());
        
        // Initialize with null values to be filled in on demand
        for (int i = 0; i < cards.size(); i++) {
            cachedPanels.add(null);
        }
    }
    
    @Override
    public int getSize() {
        return cards.size();
    }
    
    @Override
    public JPanel getElementAt(int index) {
        // Check if the panel is already created
        JPanel panel = cachedPanels.get(index);
        if (panel == null) {
            // Create the panel on demand
            panel = panelCreator.apply(cards.get(index));
            cachedPanels.set(index, panel);
            
            // Pre-load next batch of panels in background
            if (index % batchSize == 0) {
                preloadBatch(index + 1, Math.min(index + batchSize, cards.size()));
            }
        }
        return panel;
    }
    
    /**
     * Preloads a batch of panels in the background.
     * 
     * @param startIndex The start index of the batch
     * @param endIndex The end index of the batch (exclusive)
     */
    private void preloadBatch(int startIndex, int endIndex) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                for (int i = startIndex; i < endIndex; i++) {
                    if (cachedPanels.get(i) == null) {
                        JPanel panel = panelCreator.apply(cards.get(i));
                        cachedPanels.set(i, panel);
                    }
                }
                return null;
            }
        };
        worker.execute();
    }
    
    /**
     * Clears the cached panels.
     */
    public void clearCache() {
        for (int i = 0; i < cachedPanels.size(); i++) {
            cachedPanels.set(i, null);
        }
        fireContentsChanged(this, 0, getSize() - 1);
    }
}