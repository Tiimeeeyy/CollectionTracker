package gui;

import api.APIGet; // Assuming APIGet uses WebClient now as per previous steps
import data.Card;
import database.CardRepository; // Import the repository
import util.ImageCache;
import util.LazyListModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// No need for java.net.* imports if APIGet is refactored
import java.util.List;
import java.util.logging.Level; // Import Level for logging
import lombok.extern.java.Log; // Assuming Lombok @Log for java.util.logging

@Log // Use Lombok logging
public class CardGUI extends JFrame {
    // Dark mode colors (existing)
    static final Color PRIMARY_COLOR = new Color(25, 118, 210);
    static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    static final Color TEXT_COLOR = new Color(220, 220, 220);
    static final Color PANEL_COLOR = new Color(50, 50, 50);
    static final Color TITLE_COLOR = new Color(255, 255, 255);
    private static final Color BUTTON_COLOR = new Color(70, 130, 180); // Unused?
    private static final Color BUTTON_TEXT_COLOR = new Color(255, 255, 255); // Unused?
    private static final Color BLACK_TEXT_COLOR = new Color(0, 0, 0); // Keep for specific components

    private JPanel cardContainer;
    private CardLayout cardLayout;

    // --- Database Integration Fields ---
    private final CardRepository cardRepository;
    private Card currentlyDisplayedCard; // Keep track of the card being shown in the detail view
    // Reference to the 'search by ID' content panel to update it from search-by-name results
    private JPanel searchByIdContentPanel;
    // --- End Database Integration Fields ---


    // --- Constructor ---
    public CardGUI(CardRepository cardRepository) { // Accept repository
        this.cardRepository = cardRepository;
        initUI();
    }

    private void initUI() {
        setTitle("Pokemon Card Collection Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Set dark mode look and feel (existing)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Apply custom dark theme colors more consistently
            UIManager.put("Panel.background", BACKGROUND_COLOR);
            UIManager.put("OptionPane.background", BACKGROUND_COLOR);
            UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
            UIManager.put("Label.foreground", TEXT_COLOR);
            UIManager.put("Button.background", PANEL_COLOR); // Darker button background
            UIManager.put("Button.foreground", TEXT_COLOR); // Light text on buttons
            UIManager.put("TextField.background", Color.WHITE); // Keep text fields white for readability
            UIManager.put("TextField.foreground", BLACK_TEXT_COLOR);
            UIManager.put("TextArea.background", Color.WHITE); // Keep text areas white
            UIManager.put("TextArea.foreground", BLACK_TEXT_COLOR);
            UIManager.put("CheckBox.background", BACKGROUND_COLOR); // Checkbox background
            UIManager.put("CheckBox.foreground", TEXT_COLOR);      // Checkbox text
            UIManager.put("List.background", PANEL_COLOR); // List background
            UIManager.put("List.foreground", TEXT_COLOR); // List text
            UIManager.put("List.selectionBackground", BUTTON_COLOR); // Use button color for selection
            UIManager.put("List.selectionForeground", BUTTON_TEXT_COLOR);
            UIManager.put("ScrollPane.background", BACKGROUND_COLOR);
            UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
            UIManager.put("Viewport.background", BACKGROUND_COLOR); // Ensure scroll pane viewport is dark


        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to set Look and Feel", e);
        }


        // Create card layout for multiple screens (existing)
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setBackground(BACKGROUND_COLOR);

        // Add welcome screen (existing)
        cardContainer.add(createWelcomeScreen(), "welcome");

        // Add search screen (modified to store content panel reference)
        cardContainer.add(createSearchScreen(), "search");

        // Add search by name screen (existing)
        cardContainer.add(createSearchByNameScreen(), "searchByName");

        // Show welcome screen first (existing)
        cardLayout.show(cardContainer, "welcome");

        add(cardContainer);
    }

    private JPanel createWelcomeScreen() {
        // --- Same as original createWelcomeScreen ---
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));

        JLabel titleLabel = new JLabel("Welcome to Pokemon Card Collection Tracker");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("Search and view information about Pokemon cards");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        descLabel.setForeground(TEXT_COLOR);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton searchButton = new JButton("Search by ID");
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        // searchButton.setBackground(Color.WHITE); // Use themed color
        // searchButton.setForeground(BLACK_TEXT_COLOR); // Use themed color
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchButton.setMaximumSize(new Dimension(200, 50));
        searchButton.addActionListener(e -> cardLayout.show(cardContainer, "search"));

        JButton searchByNameButton = new JButton("Search by Name");
        searchByNameButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        // searchByNameButton.setBackground(Color.WHITE); // Use themed color
        // searchByNameButton.setForeground(BLACK_TEXT_COLOR); // Use themed color
        searchByNameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchByNameButton.setMaximumSize(new Dimension(200, 50));
        searchByNameButton.addActionListener(e -> cardLayout.show(cardContainer, "searchByName"));

        panel.add(Box.createVerticalGlue());
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(descLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 50)));
        panel.add(searchButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(searchByNameButton);
        panel.add(Box.createVerticalGlue());

        return panel;
        // --- End of original createWelcomeScreen ---
    }

    // Modified to store a reference to the content panel
    private JPanel createSearchScreen() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Search panel at the top
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel searchLabel = new JLabel("Enter Card ID: ");
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        // searchLabel.setForeground(BLACK_TEXT_COLOR); // Use themed color

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JButton goButton = new JButton("Go");
        goButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        // goButton.setBackground(Color.WHITE); // Use themed color
        // goButton.setForeground(BLACK_TEXT_COLOR); // Use themed color

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        // backButton.setBackground(Color.WHITE); // Use themed color
        // backButton.setForeground(BLACK_TEXT_COLOR); // Use themed color
        backButton.addActionListener(e -> {
            currentlyDisplayedCard = null; // Clear current card when going back
            cardLayout.show(cardContainer, "welcome");
        });


        searchPanel.add(backButton);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchLabel);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchField);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(goButton);

        // Content panel (will be populated when a card is searched)
        // Store reference to this panel
        searchByIdContentPanel = new JPanel(new BorderLayout());
        searchByIdContentPanel.setBackground(BACKGROUND_COLOR);

        // Initial message
        JLabel initialLabel = new JLabel("Enter a card ID and click Go to search");
        initialLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        initialLabel.setForeground(TEXT_COLOR);
        initialLabel.setHorizontalAlignment(JLabel.CENTER);
        searchByIdContentPanel.add(initialLabel, BorderLayout.CENTER);

        // Add action to the Go button
        goButton.addActionListener(e -> { // Use lambda expression
            String cardId = searchField.getText().trim();
            if (cardId.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Please enter a card ID",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Show loading message
            setLoadingState(searchByIdContentPanel, "Loading card data...");

            // Use SwingWorker to fetch card data in background
            SwingWorker<Card, Void> worker = new SwingWorker<Card, Void>() {
                @Override
                protected Card doInBackground() throws Exception { // Can throw Exception
                    try {
                        // Assuming APIGet uses WebClient and returns CompletableFuture
                        return APIGet.getCardDataById(cardId).join();
                    } catch (Exception apiEx) {
                        log.log(Level.SEVERE, "API call failed for ID " + cardId, apiEx);
                        // Rethrow or handle appropriately to signal error to done()
                        throw new RuntimeException("Failed to fetch card data: " + apiEx.getMessage(), apiEx);
                    }
                }

                @Override
                protected void done() {
                    try {
                        Card card = get(); // This can throw exceptions from doInBackground
                        if (card != null) {
                            currentlyDisplayedCard = card; // Store the fetched card
                            // Create and display the card panel
                            updateContentPanel(searchByIdContentPanel, createCardDisplayPanel(card));
                        } else {
                            // Show error message if card not found (API returned null)
                            setErrorState(searchByIdContentPanel, "Card not found. Please check the ID.");
                        }
                    } catch (Exception ex) {
                        // Handle exceptions from doInBackground or get()
                        log.log(Level.SEVERE, "Error retrieving card data for ID " + cardId, ex);
                        setErrorState(searchByIdContentPanel, "Error fetching card data: " + ex.getMessage());
                        currentlyDisplayedCard = null; // Ensure no card is considered displayed on error
                    }
                }
            };
            worker.execute();
        });

        // Add components to main panel
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(searchByIdContentPanel, BorderLayout.CENTER); // Add the stored content panel

        return panel;
    }

    // Creates the panel containing image, details, and collected checkbox
    private JPanel createCardDisplayPanel(Card card) {
        JPanel cardDisplayPanel = new JPanel(new BorderLayout(10, 10));
        cardDisplayPanel.setBackground(BACKGROUND_COLOR);
        cardDisplayPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add some padding

        // Card image panel (left side)
        JPanel imagePanel = createImagePanel(card);
        cardDisplayPanel.add(imagePanel, BorderLayout.WEST);

        // Card details panel (right side) - includes checkbox
        JPanel detailsPanel = createDetailsPanel(card);
        // Wrap details panel in a scroll pane if content might overflow
        JScrollPane detailsScrollPane = new JScrollPane(detailsPanel);
        detailsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        detailsScrollPane.setBackground(BACKGROUND_COLOR);
        detailsScrollPane.getViewport().setBackground(BACKGROUND_COLOR); // Viewport background
        detailsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        detailsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        cardDisplayPanel.add(detailsScrollPane, BorderLayout.CENTER);

        return cardDisplayPanel;
    }


    // Creates only the image part of the display
    private JPanel createImagePanel(Card card) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(0, 0, 0, 10));
        // Let the layout manager determine the size, or set preferred size if needed
        panel.setPreferredSize(new Dimension(300, 450)); // Adjusted size slightly

        if (card != null && card.getImageInfo() != null && card.getImageInfo().getLarge() != null) {
            JLabel imageLabel = new JLabel();
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            panel.add(imageLabel, BorderLayout.CENTER);

            String imageUrl = card.getImageInfo().getLarge();
            // Load image asynchronously
            ImageCache.loadImageAsync(imageUrl, -1, 420, image -> { // Slightly larger height
                if (image != null) {
                    imageLabel.setIcon(new ImageIcon(image));
                    panel.revalidate();
                    panel.repaint();
                } else {
                    // Handle image loading error if needed
                    imageLabel.setText("Image load error");
                    imageLabel.setForeground(Color.RED);
                }
            });


        } else {
            JLabel noImageLabel = new JLabel("No image available");
            noImageLabel.setForeground(TEXT_COLOR);
            noImageLabel.setHorizontalAlignment(JLabel.CENTER);
            panel.add(noImageLabel, BorderLayout.CENTER);
        }

        return panel;
    }

    // Creates the details part + collected checkbox
    private JPanel createDetailsPanel(Card card) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0)); // Add bottom padding

        if (card == null) {
            // Handle case where card is null (should not happen if called correctly)
            panel.add(new JLabel("Error: No card data to display."));
            return panel;
        }

        // Card name (title)
        JLabel nameLabel = new JLabel(card.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20)); // Slightly smaller title
        nameLabel.setForeground(TITLE_COLOR);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(nameLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- Card Details ---
        addDetailRow(panel, "Card ID:", card.getId());
        addDetailRow(panel, "Supertype:", card.getSupertype());
        if (card.getTypes() != null && !card.getTypes().isEmpty()) {
            addDetailRow(panel, "Types:", String.join(", ", card.getTypes()));
        }
        if (card.getSubtypes() != null && !card.getSubtypes().isEmpty()) {
            addDetailRow(panel, "Subtypes:", String.join(", ", card.getSubtypes()));
        }
        if (card.getSetInfo() != null) {
            addDetailRow(panel, "Set:", card.getSetInfo().getSetName() + " (" + card.getSetInfo().getSetId() + ")");
            addDetailRow(panel, "Set Series:", card.getSetInfo().getSeries());
        }
        addDetailRow(panel, "Number:", card.getNumber());
        addDetailRow(panel, "Rarity:", card.getRarity());

        // Card market info - show only the BigDecimal value
        if (card.getCardMarket() != null && card.getCardMarket().getPriceInfo() != null
                && card.getCardMarket().getPriceInfo().getAverageSellPrice() != null) {
            // Assuming GermanProLow is the desired price field
            addDetailRow(panel, "Market Price (Low):", "€" + card.getCardMarket().getPriceInfo().getAverageSellPrice().toString());
        } else {
            addDetailRow(panel, "Market Price:", "N/A");
        }


        // --- Add Collected Checkbox ---
        panel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacing before checkbox

        JCheckBox collectedCheckBox = new JCheckBox("Collected");
        collectedCheckBox.setFont(new Font("SansSerif", Font.BOLD, 14));
        // collectedCheckBox.setForeground(TEXT_COLOR); // Use themed color
        // collectedCheckBox.setBackground(BACKGROUND_COLOR); // Use themed color
        collectedCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Set initial state based on whether the card is already in the DB
        // Perform DB check carefully, handle potential null repository
        boolean isCollected = false;
        if (cardRepository != null) {
            try {
                isCollected = cardRepository.existsById(card.getId());
            } catch (Exception dbEx) {
                log.log(Level.SEVERE, "Failed to check collection status for card " + card.getId(), dbEx);
                // Keep checkbox unchecked on error
            }
        } else {
            log.warning("CardRepository is null when creating details panel.");
        }
        collectedCheckBox.setSelected(isCollected);


        collectedCheckBox.addActionListener(e -> {
            // Use the card object passed to this method, NOT currentlyDisplayedCard
            // as this panel might be created for a list item preview initially.
            // The actual saving should happen based on the card shown in the *main* detail view.
            // Let's refine this: The checkbox listener should act on 'currentlyDisplayedCard'
            // which is set when a card is loaded into the main 'searchByIdContentPanel'.

            if (currentlyDisplayedCard != null && currentlyDisplayedCard.getId().equals(card.getId())) {
                // Only proceed if the checkbox corresponds to the fully displayed card
                if (cardRepository == null) {
                    log.severe("CardRepository is null. Cannot save/delete collection item.");
                    JOptionPane.showMessageDialog(panel, "Database connection error.", "DB Error", JOptionPane.ERROR_MESSAGE);
                    collectedCheckBox.setSelected(!collectedCheckBox.isSelected()); // Revert state
                    return;
                }
                try {
                    if (collectedCheckBox.isSelected()) {
                        // Save to database
                        log.info("Attempting to save card to collection: " + currentlyDisplayedCard.getId());
                        cardRepository.save(currentlyDisplayedCard); // Save the card currently displayed in detail view
                        log.info("Saved card to collection: " + currentlyDisplayedCard.getId());
                        // Optional: User feedback
                        // JOptionPane.showMessageDialog(panel, "Card added to collection!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // Remove from database
                        log.info("Attempting to remove card from collection: " + currentlyDisplayedCard.getId());
                        cardRepository.deleteById(currentlyDisplayedCard.getId());
                        log.info("Removed card from collection: " + currentlyDisplayedCard.getId());
                        // Optional: User feedback
                        // JOptionPane.showMessageDialog(panel, "Card removed from collection.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception dbEx) {
                    log.log(Level.SEVERE, "Database operation failed for card " + currentlyDisplayedCard.getId(), dbEx);
                    JOptionPane.showMessageDialog(panel, "Database error: " + dbEx.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
                    // Revert checkbox state on error
                    collectedCheckBox.setSelected(!collectedCheckBox.isSelected());
                }

            } else {
                // This might happen if the checkbox is somehow clicked when no card is fully displayed,
                // or if the card in the panel is not the one in the main view (e.g., list preview)
                log.warning("Collected checkbox action ignored: currentlyDisplayedCard mismatch or null.");
                // Optionally revert the checkbox state if it's not supposed to be interactive here
                // collectedCheckBox.setSelected(!collectedCheckBox.isSelected());
            }
        });

        panel.add(collectedCheckBox);
        // --- End Checkbox ---

        panel.add(Box.createVerticalGlue()); // Push content to the top

        return panel;
    }

    // Helper to add a label-value pair row
    private void addDetailRow(JPanel panel, String label, String value) {
        if (value == null || value.isEmpty()) {
            return; // Don't add row if value is missing
        }

        JPanel rowPanel = new JPanel(new BorderLayout(5, 0)); // Reduced gap
        rowPanel.setBackground(BACKGROUND_COLOR);
        // Set a maximum size to prevent excessive height
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("SansSerif", Font.BOLD, 12)); // Smaller font
        labelComponent.setForeground(TEXT_COLOR);
        // Set preferred width for label column for alignment
        labelComponent.setPreferredSize(new Dimension(80, 20)); // Adjust width as needed

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("SansSerif", Font.PLAIN, 12)); // Smaller font
        valueComponent.setForeground(TEXT_COLOR);
        // Allow value component to take remaining space
        valueComponent.setPreferredSize(new Dimension(100, 20)); // Minimum width


        rowPanel.add(labelComponent, BorderLayout.WEST);
        rowPanel.add(valueComponent, BorderLayout.CENTER);

        panel.add(rowPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 3))); // Reduced spacing
    }


    private JPanel createSearchByNameScreen() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Search panel at the top (similar to search by ID)
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel searchLabel = new JLabel("Enter Card Name: ");
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        // searchLabel.setForeground(BLACK_TEXT_COLOR); // Use themed color

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        // searchButton.setBackground(Color.WHITE); // Use themed color
        // searchButton.setForeground(BLACK_TEXT_COLOR); // Use themed color

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        // backButton.setBackground(Color.WHITE); // Use themed color
        // backButton.setForeground(BLACK_TEXT_COLOR); // Use themed color
        backButton.addActionListener(e -> {
            currentlyDisplayedCard = null; // Clear current card when going back
            cardLayout.show(cardContainer, "welcome");
        });

        searchPanel.add(backButton);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchLabel);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchField);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchButton);

        // Results panel using BoxLayout for list items
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS)); // Use BoxLayout
        resultsPanel.setBackground(BACKGROUND_COLOR);

        // Scroll pane for results
        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);

        // Initial message in resultsPanel
        setInitialState(resultsPanel, "Enter a card name and click Search");


        // Add action to the Search button
        searchButton.addActionListener(e -> {
            String cardName = searchField.getText().trim();
            if (cardName.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Please enter a card name",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            setLoadingState(resultsPanel, "Searching for cards...");

            // Use SwingWorker to fetch card data in background
            SwingWorker<List<Card>, Void> worker = new SwingWorker<List<Card>, Void>() {
                @Override
                protected List<Card> doInBackground() throws Exception { // Allow exception throwing
                    try {
                        // Use APIGet (assuming it's refactored for WebClient)
                        return APIGet.queryCardsByName(cardName).join();
                    } catch (Exception apiEx) {
                        log.log(Level.SEVERE, "API search failed for name '" + cardName + "'", apiEx);
                        throw new RuntimeException("Failed to search cards: " + apiEx.getMessage(), apiEx);
                    }
                }

                @Override
                protected void done() {
                    resultsPanel.removeAll(); // Clear loading/previous results
                    resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS)); // Ensure layout is BoxLayout

                    try {
                        List<Card> cards = get(); // Can throw exceptions

                        if (cards != null && !cards.isEmpty()) {
                            // Use LazyListModel for efficient rendering if list is very large
                            // For moderate lists, adding panels directly might be simpler:
                            for (Card card : cards) {
                                JPanel listItemPanel = createCardListItem(card); // Get the panel for the card
                                // Add listener to each item to show details
                                listItemPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                                        currentlyDisplayedCard = card; // Set the selected card as current
                                        // Update the content panel on the "Search by ID" screen
                                        updateContentPanel(searchByIdContentPanel, createCardDisplayPanel(card));
                                        // Switch to the detail view screen
                                        cardLayout.show(cardContainer, "search");
                                    }
                                });
                                resultsPanel.add(listItemPanel);
                                resultsPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Add spacing between items
                            }
                        } else {
                            // Show no results message
                            setErrorState(resultsPanel, "No cards found matching that name.");
                        }

                    } catch (Exception ex) {
                        log.log(Level.SEVERE, "Error retrieving search results for name '" + cardName + "'", ex);
                        setErrorState(resultsPanel, "Error searching for cards: " + ex.getMessage());
                    } finally {
                        resultsPanel.revalidate();
                        resultsPanel.repaint();
                    }
                }
            };
            worker.execute();
        });

        // Add components to main panel
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER); // Add scroll pane containing results panel

        return panel;
    }

    // Creates a JPanel representing a single card in the search-by-name list
    private JPanel createCardListItem(Card card) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BACKGROUND_COLOR), // Bottom border for separation
                BorderFactory.createEmptyBorder(10, 10, 10, 10)) // Padding
        );
        // Set a maximum size to prevent items from stretching vertically
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); // Adjust height as needed
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Indicate clickable


        // Card image (small)
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(PANEL_COLOR); // Match item background
        imagePanel.setPreferredSize(new Dimension(60, 80)); // Fixed size for image area


        if (card.getImageInfo() != null && card.getImageInfo().getSmall() != null) {
            JLabel imageLabel = new JLabel(); // Placeholder label
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            imagePanel.add(imageLabel, BorderLayout.CENTER);

            String imageUrl = card.getImageInfo().getSmall();
            // Load image async
            ImageCache.loadImageAsync(imageUrl, 60, -1, image -> { // Scale width, maintain aspect ratio
                if (image != null) {
                    imageLabel.setIcon(new ImageIcon(image));
                    imagePanel.revalidate(); // Revalidate image panel only
                    imagePanel.repaint();
                } else {
                    imageLabel.setText("N/A");
                    imageLabel.setForeground(TEXT_COLOR);
                }
            });


        } else {
            JLabel noImageLabel = new JLabel("No Img");
            noImageLabel.setForeground(TEXT_COLOR);
            noImageLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
            noImageLabel.setHorizontalAlignment(JLabel.CENTER);
            imagePanel.add(noImageLabel, BorderLayout.CENTER);
        }

        // Card name and details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(PANEL_COLOR); // Match item background
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Vertical padding

        JLabel nameLabel = new JLabel(card.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailsPanel.add(nameLabel);

        // Add set name and rarity
        String setInfo = "Set: " + (card.getSetInfo() != null ? card.getSetInfo().getSetName() : "N/A");
        String rarityInfo = "Rarity: " + (card.getRarity() != null ? card.getRarity() : "N/A");

        JLabel setLabel = new JLabel(setInfo);
        setLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        setLabel.setForeground(TEXT_COLOR);
        setLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rarityLabel = new JLabel(rarityInfo);
        rarityLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        rarityLabel.setForeground(TEXT_COLOR);
        rarityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);


        detailsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        detailsPanel.add(setLabel);
        detailsPanel.add(rarityLabel);


        panel.add(imagePanel, BorderLayout.WEST);
        panel.add(detailsPanel, BorderLayout.CENTER);

        return panel;
    }

    // --- Helper Methods for Panel States ---

    private void setPanelState(JPanel targetPanel, String message, Color textColor) {
        targetPanel.removeAll();
        targetPanel.setLayout(new BorderLayout()); // Ensure BorderLayout for centering
        JLabel label = new JLabel(message);
        label.setFont(new Font("SansSerif", Font.ITALIC, 16));
        label.setForeground(textColor);
        label.setHorizontalAlignment(JLabel.CENTER);
        targetPanel.add(label, BorderLayout.CENTER);
        targetPanel.revalidate();
        targetPanel.repaint();
    }


    private void setLoadingState(JPanel targetPanel, String message) {
        setPanelState(targetPanel, message, TEXT_COLOR);
    }

    private void setErrorState(JPanel targetPanel, String errorMessage) {
        setPanelState(targetPanel, errorMessage, new Color(255, 100, 100)); // Red for errors
    }

    private void setInitialState(JPanel targetPanel, String message) {
        setPanelState(targetPanel, message, TEXT_COLOR);
    }


    private void updateContentPanel(JPanel targetPanel, JPanel newContent) {
        targetPanel.removeAll();
        targetPanel.setLayout(new BorderLayout()); // Ensure layout manager
        targetPanel.add(newContent, BorderLayout.CENTER);
        targetPanel.revalidate();
        targetPanel.repaint();
    }

    // --- Static displayCard method (kept for potential external use, but might be redundant) ---
    // Consider removing if all display logic is handled internally
    /*
    public static void displayCard(Card card) {
        // This needs access to an instance of CardGUI and its components.
        // Making it static is problematic without significant refactoring
        // or passing GUI references around.
        // It's generally better to handle display updates within the CardGUI instance methods.
        log.warning("Static displayCard method called - may not function correctly without GUI instance.");

        // Example (requires instance access, e.g., through Singleton or dependency injection):
        // SwingUtilities.invokeLater(() -> {
        //     CardGUI instance = CardGUI.getInstance(); // Assuming a getInstance() method exists
        //     if (instance != null) {
        //         instance.currentlyDisplayedCard = card;
        //         instance.updateContentPanel(instance.searchByIdContentPanel, instance.createCardDisplayPanel(card));
        //         instance.cardLayout.show(instance.cardContainer, "search");
        //     }
        // });
    }
    */
}
