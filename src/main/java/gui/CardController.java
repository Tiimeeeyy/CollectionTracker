package gui;

import api.APIGet;
import data.Card;
import database.CardRepository;
import lombok.Setter;
import lombok.extern.java.Log;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList; // Import ArrayList
import java.util.Collections; // Import Collections
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Log
public class CardController {
    private final CardRepository cardRepository;

    // Setters for main layout components and the detail panel
    @Setter private JPanel cardContainer;
    @Setter private CardLayout cardLayout;
    @Setter private SearchByIdPanel searchByIdPanel; // Needed to navigate TO the detail view

    private Card currentlyDisplayedCard; // Card shown in the SearchByIdPanel's detail view

    public CardController(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    /**
     * Navigates to a specific screen/panel in the CardLayout.
     * Clears the currently displayed card if navigating away from a detail view implicitly.
     * @param screenName The name of the panel to show (must match the name used in CardLayout.add).
     */
    public void navigateTo(String screenName) {
        // Clear currently displayed card if we are not navigating TO the detail screen
        if (!screenName.equals("search")) {
            // Or add more specific checks if needed, e.g., if coming FROM "search"
            currentlyDisplayedCard = null;
        }
        // Show the requested panel
        if (cardLayout != null && cardContainer != null) {
            cardLayout.show(cardContainer, screenName);
        } else {
            log.severe("CardLayout or CardContainer is null in navigateTo. Cannot switch panels.");
        }
    }

    /**
     * Searches for a card by its unique ID using the API.
     * Updates the content panel of the SearchByIdPanel.
     * @param cardId The ID of the card to search for.
     * @param contentPanel The JPanel within SearchByIdPanel to update.
     */
    public void searchCardById(String cardId, JPanel contentPanel) {
        // Validate input
        if (cardId == null || cardId.trim().isEmpty()) {
            UIUtils.showErrorMessage(contentPanel, "Please enter a card ID.");
            UIUtils.setInitialState(contentPanel, "Enter a card ID and click Go to search"); // Reset panel
            return;
        }

        // Set loading state
        UIUtils.setLoadingState(contentPanel, "Loading card data for ID: " + cardId + "...");

        // Perform API call in background using SwingWorker
        SwingWorker<Card, Void> worker = new SwingWorker<>() {
            @Override
            protected Card doInBackground() throws Exception {
                try {
                    // Call the asynchronous API method and wait for the result
                    return APIGet.getCardDataById(cardId).join();
                } catch (Exception apiEx) {
                    log.log(Level.SEVERE, "API call failed for ID " + cardId, apiEx);
                    // Propagate exception to be caught in done()
                    throw new RuntimeException("Failed to fetch card data: " + apiEx.getMessage(), apiEx);
                }
            }

            @Override
            protected void done() {
                try {
                    Card card = get(); // Retrieve the result, may throw exceptions from doInBackground
                    if (card != null) {
                        // If successful, display the card details
                        displayCardDetails(card, contentPanel);
                    } else {
                        // If API returned null (or empty optional), card wasn't found
                        UIUtils.setErrorState(contentPanel, "Card not found for ID: " + cardId);
                        currentlyDisplayedCard = null; // Ensure no card is considered displayed
                    }
                } catch (Exception ex) {
                    // Handle exceptions during background task or result retrieval
                    log.log(Level.SEVERE, "Error retrieving card data for ID " + cardId, ex);
                    // Display error message in the panel
                    UIUtils.setErrorState(contentPanel, "Error fetching card data: " + ex.getCause().getMessage());
                    currentlyDisplayedCard = null; // Ensure no card is considered displayed
                }
            }
        };
        worker.execute(); // Start the worker thread
    }

    /**
     * Searches for cards by name using the API.
     * Populates the provided resultsPanel with list items.
     * @param cardName The name (or partial name) of the card to search for.
     * @param resultsPanel The JPanel to display the list of results.
     */
    public void searchCardsByName(String cardName, JPanel resultsPanel) {
        // Validate input
        if (cardName == null || cardName.trim().isEmpty()) {
            UIUtils.showErrorMessage(resultsPanel, "Please enter a card name.");
            UIUtils.setInitialState(resultsPanel, "Enter a card name and click Search"); // Reset panel
            return;
        }

        // Set loading state
        UIUtils.setLoadingState(resultsPanel, "Searching for cards named: " + cardName + "...");

        // Perform API call in background
        SwingWorker<List<Card>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Card> doInBackground() throws Exception {
                try {
                    // Call API and wait for result
                    return APIGet.queryCardsByName(cardName).join();
                } catch (Exception apiEx) {
                    log.log(Level.SEVERE, "API search failed for name '" + cardName + "'", apiEx);
                    throw new RuntimeException("Failed to search cards: " + apiEx.getMessage(), apiEx);
                }
            }

            @Override
            protected void done() {
                try {
                    List<Card> cards = get(); // Get results
                    // Display the results (handles null/empty list internally)
                    displayCardSearchResults(cards, resultsPanel, "No cards found matching name: " + cardName);
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error retrieving search results for name '" + cardName + "'", ex);
                    // Display error message
                    UIUtils.setErrorState(resultsPanel, "Error searching for cards: " + ex.getCause().getMessage());
                }
            }
        };
        worker.execute();
    }

    // --- NEW METHODS ---

    /**
     * Searches for cards by set ID using the API.
     * Populates the provided resultsPanel with list items.
     * @param setId The ID of the set to search for (e.g., "swsh11").
     * @param resultsPanel The JPanel to display the list of results.
     */
    public void searchCardsBySet(String setId, JPanel resultsPanel) {
        // Basic validation already done in the panel, but double-check
        if (setId == null || setId.trim().isEmpty()) {
            UIUtils.showErrorMessage(resultsPanel, "Set ID cannot be empty.");
            UIUtils.setInitialState(resultsPanel, "Enter a Set ID (e.g., 'swsh11') and click Search");
            return;
        }

        UIUtils.setLoadingState(resultsPanel, "Searching for cards in set: " + setId + "...");

        SwingWorker<List<Card>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Card> doInBackground() throws Exception {
                try {
                    return APIGet.queryCardsBySet(setId).join();
                } catch (Exception apiEx) {
                    log.log(Level.SEVERE, "API search failed for set '" + setId + "'", apiEx);
                    throw new RuntimeException("Failed to search cards by set: " + apiEx.getMessage(), apiEx);
                }
            }

            @Override
            protected void done() {
                try {
                    List<Card> cards = get();
                    displayCardSearchResults(cards, resultsPanel, "No cards found for set ID: " + setId);
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error retrieving search results for set '" + setId + "'", ex);
                    UIUtils.setErrorState(resultsPanel, "Error searching set: " + ex.getCause().getMessage());
                }
            }
        };
        worker.execute();
    }

    /**
     * Searches for cards by National Pokédex number range using the API.
     * Populates the provided resultsPanel with list items.
     * @param startStr The starting Pokédex number as a string.
     * @param endStr The ending Pokédex number as a string (can be empty or same as start).
     * @param resultsPanel The JPanel to display the list of results.
     */
    public void searchCardsByPokedex(String startStr, String endStr, JPanel resultsPanel) {
        int startNum, endNum;
        try {
            startNum = Integer.parseInt(startStr);
            // If endStr is empty or null, use startNum as the end of the range
            endNum = (endStr == null || endStr.isEmpty()) ? startNum : Integer.parseInt(endStr);

            if (startNum <= 0 || endNum <= 0) {
                UIUtils.showErrorMessage(resultsPanel, "Pokédex numbers must be positive.");
                return;
            }
            if (startNum > endNum) {
                UIUtils.showErrorMessage(resultsPanel, "Starting Pokédex number cannot be greater than the ending number.");
                return;
            }
        } catch (NumberFormatException nfe) {
            UIUtils.showErrorMessage(resultsPanel, "Please enter valid numbers for the Pokédex range.");
            return;
        }

        String rangeQuery = startNum == endNum ? String.valueOf(startNum) : startNum + " to " + endNum;
        UIUtils.setLoadingState(resultsPanel, "Searching Pokédex range: " + rangeQuery + "...");

        final int finalStartNum = startNum;
        final int finalEndNum = endNum;

        SwingWorker<List<Card>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Card> doInBackground() throws Exception {
                try {
                    // Pass the parsed integers to the API method
                    return APIGet.pokedexSearch(finalStartNum, finalEndNum).join();
                } catch (Exception apiEx) {
                    log.log(Level.SEVERE, "API search failed for Pokédex range " + rangeQuery, apiEx);
                    throw new RuntimeException("Failed to search Pokédex range: " + apiEx.getMessage(), apiEx);
                }
            }

            @Override
            protected void done() {
                try {
                    List<Card> cards = get();
                    displayCardSearchResults(cards, resultsPanel, "No cards found for Pokédex range: " + rangeQuery);
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error retrieving search results for Pokédex range " + rangeQuery, ex);
                    UIUtils.setErrorState(resultsPanel, "Error searching Pokédex: " + ex.getCause().getMessage());
                }
            }
        };
        worker.execute();
    }

    /**
     * Loads all saved cards from the database and displays them in the collection panel.
     * @param collectionPanel The JPanel within ViewCollectionPanel to update.
     */
    public void loadCollectionView(JPanel collectionPanel) {
        if (cardRepository == null) {
            log.severe("CardRepository is null. Cannot load collection.");
            UIUtils.setErrorState(collectionPanel, "Database connection error.");
            return;
        }

        UIUtils.setLoadingState(collectionPanel, "Loading your collection...");

        SwingWorker<List<Card>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Card> doInBackground() throws Exception {
                try {
                    // Fetch all cards from the repository
                    return cardRepository.findAll();
                } catch (Exception dbEx) {
                    log.log(Level.SEVERE, "Database query failed for findAll", dbEx);
                    throw new RuntimeException("Failed to load collection from database: " + dbEx.getMessage(), dbEx);
                }
            }

            @Override
            protected void done() {
                try {
                    List<Card> cards = get();
                    // Use the same display method, provide specific empty message
                    displayCardSearchResults(cards, collectionPanel, "Your collection is empty. Add cards using the search features!");
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error retrieving collection from database", ex);
                    UIUtils.setErrorState(collectionPanel, "Error loading collection: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    // --- Helper and Action Methods ---

    /**
     * Displays a list of cards in a target panel.
     * Clears the panel, adds clickable CardListItem components for each card.
     * Handles empty list or null input by showing an appropriate message.
     *
     * @param cards The list of cards to display. Can be null or empty.
     * @param targetPanel The JPanel to populate with card list items.
     * @param emptyMessage The message to display if the list is null or empty.
     */
    private void displayCardSearchResults(List<Card> cards, JPanel targetPanel, String emptyMessage) {
        // Ensure updates happen on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            targetPanel.removeAll(); // Clear previous content (loading message, old results)
            // Ensure layout is still BoxLayout for vertical stacking
            targetPanel.setLayout(new BoxLayout(targetPanel, BoxLayout.Y_AXIS));

            if (cards != null && !cards.isEmpty()) {
                log.info("Displaying " + cards.size() + " cards in the list.");
                for (Card card : cards) {
                    // Create the list item panel for the card
                    JPanel listItemPanel = CardComponentFactory.createCardListItem(card);

                    // Add mouse listener to handle clicks on the list item
                    listItemPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            // When clicked, display the full details of this card
                            // in the SearchByIdPanel's content area and navigate there.
                            if (searchByIdPanel != null) {
                                displayCardDetails(card, searchByIdPanel.getContentPanel());
                                navigateTo("search"); // Navigate to the detail view screen
                            } else {
                                log.warning("SearchByIdPanel reference is null. Cannot navigate to details.");
                            }
                        }
                        // Optional: Add hover effect (change background)
                        @Override
                        public void mouseEntered(java.awt.event.MouseEvent e) {
                            listItemPanel.setBackground(CardGUI.PANEL_COLOR.brighter()); // Slightly lighter on hover
                        }
                        @Override
                        public void mouseExited(java.awt.event.MouseEvent e) {
                            listItemPanel.setBackground(CardGUI.PANEL_COLOR); // Restore original color
                        }
                    });
                    targetPanel.add(listItemPanel);
                    // Add a small vertical gap between items
                    targetPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                }
            } else {
                // If the list is null or empty, show the specified message
                log.info("No cards to display. Showing empty message.");
                UIUtils.setInitialState(targetPanel, emptyMessage); // Use initial state styling for empty message
            }

            // Refresh the panel layout
            targetPanel.revalidate();
            targetPanel.repaint();
        });
    }


    /**
     * Updates the content panel of the SearchByIdPanel to show the details of a specific card.
     * @param card The Card object whose details are to be displayed.
     * @param contentPanel The JPanel within SearchByIdPanel to update.
     */
    public void displayCardDetails(Card card, JPanel contentPanel) {
        if (card == null) {
            log.warning("Attempted to display details for a null card.");
            UIUtils.setErrorState(contentPanel, "Cannot display card details (null data).");
            currentlyDisplayedCard = null;
            return;
        }
        log.info("Displaying details for card: " + card.getName() + " (" + card.getId() + ")");
        // Store the card being displayed (important for the checkbox action)
        currentlyDisplayedCard = card;
        // Create the detailed card display panel using the factory
        JPanel cardPanel = CardComponentFactory.createCardDisplayPanel(card, this);
        // Update the target content panel with the new card detail panel
        UIUtils.updateContentPanel(contentPanel, cardPanel);
    }

    /**
     * Handles the action of toggling the 'Collected' checkbox in the card detail view.
     * Saves or deletes the card from the database via the CardRepository.
     * Reverts checkbox state and shows error message on failure.
     * @param card The card associated with the checkbox action.
     * @param checkBox The JCheckBox component that was toggled.
     */
    public void toggleCardCollection(Card card, JCheckBox checkBox) {
        // Ensure repository is available
        if (cardRepository == null) {
            log.severe("CardRepository is null. Cannot save/delete collection item.");
            UIUtils.showErrorMessage(checkBox.getParent(), "Database connection error."); // Show error relative to checkbox parent
            checkBox.setSelected(!checkBox.isSelected()); // Revert state
            return;
        }
        // Ensure the action corresponds to the currently displayed card
        if (card == null || !card.getId().equals(currentlyDisplayedCard.getId())) {
            log.warning("Checkbox toggle ignored: Card mismatch or null.");
            checkBox.setSelected(!checkBox.isSelected()); // Revert state
            return;
        }

        try {
            if (checkBox.isSelected()) {
                // Add card to collection
                log.info("Saving card to collection: " + card.getId() + " - " + card.getName());
                cardRepository.save(card);
                log.info("Card saved successfully.");
                // Optional: Show success feedback briefly?
            } else {
                // Remove card from collection
                log.info("Removing card from collection: " + card.getId() + " - " + card.getName());
                cardRepository.deleteById(card.getId());
                log.info("Card removed successfully.");
                // Optional: Show success feedback briefly?
            }
        } catch (Exception dbEx) {
            // Handle database errors
            log.log(Level.SEVERE, "Database operation failed for card " + card.getId(), dbEx);
            UIUtils.showErrorMessage(checkBox.getParent(), "Database error: " + dbEx.getMessage());
            // IMPORTANT: Revert checkbox state on error
            checkBox.setSelected(!checkBox.isSelected());
        }
    }

    /**
     * Checks if a card with the given ID exists in the collection database.
     * Used by CardComponentFactory to set the initial state of the 'Collected' checkbox.
     * @param cardId The ID of the card to check.
     * @return true if the card exists in the repository, false otherwise or on error.
     */
    public boolean isCardInCollection(String cardId) {
        if (cardRepository == null) {
            log.warning("CardRepository is null in isCardInCollection check.");
            return false;
        }
        try {
            // Use the repository's existsById method
            return cardRepository.existsById(cardId);
        } catch (Exception dbEx) {
            // Log database errors during the check
            log.log(Level.SEVERE, "Failed to check collection status for card " + cardId, dbEx);
            return false; // Assume not collected on error
        }
    }

    /**
     * Gets the card currently being displayed in the detail view.
     * @return The currently displayed Card object, or null.
     */
    public Card getCurrentlyDisplayedCard() {
        return currentlyDisplayedCard;
    }
}
