package gui;

import api.APIGet;
import data.Card;
import database.CardRepository;
import lombok.Setter;
import lombok.extern.java.Log;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Log
public class CardController {
    private final CardRepository cardRepository;

    @Setter private JPanel cardContainer;
    @Setter private CardLayout cardLayout;
    @Setter private SearchByIdPanel searchByIdPanel;

    private Card currentlyDisplayedCard;

    public CardController(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public void navigateTo(String screenName) {
        if (screenName.equals("welcome")) {
            currentlyDisplayedCard = null; // Clear current card
        }
        cardLayout.show(cardContainer, screenName);
    }

    public void searchCardById(String cardId, JPanel contentPanel) {
        if (cardId.isEmpty()) {
            UIUtils.showErrorMessage(contentPanel, "Please enter a card ID");
            return;
        }

        UIUtils.setLoadingState(contentPanel, "Loading card data...");

        SwingWorker<Card, Void> worker = new SwingWorker<>() {
            @Override
            protected Card doInBackground() {
                try {
                    return APIGet.getCardDataById(cardId).join();
                } catch (Exception apiEx) {
                    log.log(Level.SEVERE, "API call failed for ID " + cardId, apiEx);
                    throw new RuntimeException("Failed to fetch card data: " + apiEx.getMessage(), apiEx);
                }
            }

            @Override
            protected void done() {
                try {
                    Card card = get();
                    if (card != null) {
                        displayCardDetails(card, contentPanel);
                    } else {
                        UIUtils.setErrorState(contentPanel, "Card not found. Please check the ID.");
                    }
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error retrieving card data for ID " + cardId, ex);
                    UIUtils.setErrorState(contentPanel, "Error fetching card data: " + ex.getMessage());
                    currentlyDisplayedCard = null;
                }
            }
        };
        worker.execute();
    }

    public void searchCardsByName(String cardName, JPanel resultsPanel) {
        if (cardName.isEmpty()) {
            UIUtils.showErrorMessage(resultsPanel, "Please enter a card name");
            return;
        }

        UIUtils.setLoadingState(resultsPanel, "Searching for cards...");

        SwingWorker<List<Card>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Card> doInBackground() {
                try {
                    return APIGet.queryCardsByName(cardName).join();
                } catch (Exception apiEx) {
                    log.log(Level.SEVERE, "API search failed for name '" + cardName + "'", apiEx);
                    throw new RuntimeException("Failed to search cards: " + apiEx.getMessage(), apiEx);
                }
            }

            @Override
            protected void done() {
                try {
                    List<Card> cards = get();
                    displayCardSearchResults(cards, resultsPanel);
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Error retrieving search results for name '" + cardName + "'", ex);
                    UIUtils.setErrorState(resultsPanel, "Error searching for cards: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void displayCardSearchResults(List<Card> cards, JPanel resultsPanel) {
        resultsPanel.removeAll();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));

        if (cards != null && !cards.isEmpty()) {
            for (Card card : cards) {
                JPanel listItemPanel = CardComponentFactory.createCardListItem(card);
                listItemPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        displayCardDetails(card, searchByIdPanel.getContentPanel());
                        navigateTo("search");
                    }
                });
                resultsPanel.add(listItemPanel);
                resultsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        } else {
            UIUtils.setErrorState(resultsPanel, "No cards found matching that name.");
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    public void displayCardDetails(Card card, JPanel contentPanel) {
        currentlyDisplayedCard = card;
        JPanel cardPanel = CardComponentFactory.createCardDisplayPanel(card, this);
        UIUtils.updateContentPanel(contentPanel, cardPanel);
    }

    public void toggleCardCollection(Card card, JCheckBox checkBox) {
        if (cardRepository == null) {
            log.severe("CardRepository is null. Cannot save/delete collection item.");
            UIUtils.showErrorMessage(checkBox, "Database connection error.");
            checkBox.setSelected(!checkBox.isSelected()); // Revert state
            return;
        }

        try {
            if (checkBox.isSelected()) {
                log.info("Saving card to collection: " + card.getId());
                cardRepository.save(card);
            } else {
                log.info("Removing card from collection: " + card.getId());
                cardRepository.deleteById(card.getId());
            }
        } catch (Exception dbEx) {
            log.log(Level.SEVERE, "Database operation failed for card " + card.getId(), dbEx);
            checkBox.setSelected(!checkBox.isSelected()); // Revert state
        }
    }

    public boolean isCardInCollection(String cardId) {
        try {
            return cardRepository != null && cardRepository.existsById(cardId);
        } catch (Exception dbEx) {
            log.log(Level.SEVERE, "Failed to check collection status for card " + cardId, dbEx);
            return false;
        }
    }

    public Card getCurrentlyDisplayedCard() {
        return currentlyDisplayedCard;
    }
}