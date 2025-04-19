package gui;

import api.APIGet;
import data.Card;
import util.ImageCache;
import util.LazyListModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class CardGUI extends JFrame {
    // Dark mode colors
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color PANEL_COLOR = new Color(50, 50, 50);
    private static final Color TITLE_COLOR = new Color(255, 255, 255);
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);
    private static final Color BUTTON_TEXT_COLOR = new Color(255, 255, 255);
    private static final Color BLACK_TEXT_COLOR = new Color(0, 0, 0);

    private JPanel cardContainer;
    private CardLayout cardLayout;

    public CardGUI() {
        initUI();
    }

    private void initUI() {
        setTitle("Pokemon Card Collection Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Set dark mode look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Panel.background", BACKGROUND_COLOR);
            UIManager.put("Label.foreground", TEXT_COLOR);
            UIManager.put("TextField.background", Color.WHITE);
            UIManager.put("TextField.foreground", BLACK_TEXT_COLOR);
            UIManager.put("TextArea.background", Color.WHITE);
            UIManager.put("TextArea.foreground", BLACK_TEXT_COLOR);
            UIManager.put("Button.background", Color.WHITE);
            UIManager.put("Button.foreground", BLACK_TEXT_COLOR);

            // Use black text for components with white backgrounds
            UIManager.put("OptionPane.messageForeground", BLACK_TEXT_COLOR);
            UIManager.put("ComboBox.foreground", BLACK_TEXT_COLOR);
            UIManager.put("List.foreground", BLACK_TEXT_COLOR);
            UIManager.put("Menu.foreground", BLACK_TEXT_COLOR);
            UIManager.put("MenuItem.foreground", BLACK_TEXT_COLOR);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create card layout for multiple screens
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setBackground(BACKGROUND_COLOR);

        // Add welcome screen
        cardContainer.add(createWelcomeScreen(), "welcome");

        // Add search screen
        cardContainer.add(createSearchScreen(), "search");

        // Add search by name screen
        cardContainer.add(createSearchByNameScreen(), "searchByName");

        // Show welcome screen first
        cardLayout.show(cardContainer, "welcome");

        add(cardContainer);
    }

    private JPanel createWelcomeScreen() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));

        // Welcome title
        JLabel titleLabel = new JLabel("Welcome to Pokemon Card Collection Tracker");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Description
        JLabel descLabel = new JLabel("Search and view information about Pokemon cards");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        descLabel.setForeground(TEXT_COLOR);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Button to go to search screen
        JButton searchButton = new JButton("Search by ID");
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        searchButton.setBackground(Color.WHITE);
        searchButton.setForeground(BLACK_TEXT_COLOR);
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchButton.setMaximumSize(new Dimension(200, 50));
        searchButton.addActionListener(e -> cardLayout.show(cardContainer, "search"));

        // Button to go to search by name screen
        JButton searchByNameButton = new JButton("Search by Name");
        searchByNameButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        searchByNameButton.setBackground(Color.WHITE);
        searchByNameButton.setForeground(BLACK_TEXT_COLOR);
        searchByNameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchByNameButton.setMaximumSize(new Dimension(200, 50));
        searchByNameButton.addActionListener(e -> cardLayout.show(cardContainer, "searchByName"));

        // Add components with spacing
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
    }

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
        searchLabel.setForeground(BLACK_TEXT_COLOR);

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JButton goButton = new JButton("Go");
        goButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        goButton.setBackground(Color.WHITE);
        goButton.setForeground(BLACK_TEXT_COLOR);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(BLACK_TEXT_COLOR);
        backButton.addActionListener(e -> cardLayout.show(cardContainer, "welcome"));

        searchPanel.add(backButton);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchLabel);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchField);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(goButton);

        // Content panel (will be populated when a card is searched)
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);

        // Initial message
        JLabel initialLabel = new JLabel("Enter a card ID and click Go to search");
        initialLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        initialLabel.setForeground(TEXT_COLOR);
        initialLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(initialLabel, BorderLayout.CENTER);

        // Add action to the Go button
        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cardId = searchField.getText().trim();
                if (cardId.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, 
                        "Please enter a card ID", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Show loading message
                JLabel loadingLabel = new JLabel("Loading card data...");
                loadingLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
                loadingLabel.setForeground(TEXT_COLOR);
                loadingLabel.setHorizontalAlignment(JLabel.CENTER);
                contentPanel.removeAll();
                contentPanel.add(loadingLabel, BorderLayout.CENTER);
                contentPanel.revalidate();
                contentPanel.repaint();

                // Use SwingWorker to fetch card data in background
                SwingWorker<Card, Void> worker = new SwingWorker<Card, Void>() {
                    @Override
                    protected Card doInBackground() {
                        // Wait for the CompletableFuture to complete and get the result
                        return APIGet.getCardDataById(cardId).join();
                    }

                    @Override
                    protected void done() {
                        try {
                            Card card = get();
                            if (card != null) {
                                // Create card display panel
                                contentPanel.removeAll();

                                // Card display with image and details
                                JPanel cardDisplayPanel = new JPanel(new BorderLayout(10, 10));
                                cardDisplayPanel.setBackground(BACKGROUND_COLOR);

                                // Card image panel (left side)
                                JPanel imagePanel = createImagePanel(card);
                                cardDisplayPanel.add(imagePanel, BorderLayout.WEST);

                                // Card details panel (right side)
                                JPanel detailsPanel = createDetailsPanel(card);
                                cardDisplayPanel.add(detailsPanel, BorderLayout.CENTER);

                                contentPanel.add(cardDisplayPanel, BorderLayout.CENTER);
                                contentPanel.revalidate();
                                contentPanel.repaint();
                            } else {
                                // Show error message if card not found
                                JLabel errorLabel = new JLabel("Card not found. Please check the ID and try again.");
                                errorLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
                                errorLabel.setForeground(new Color(255, 100, 100));
                                errorLabel.setHorizontalAlignment(JLabel.CENTER);
                                contentPanel.removeAll();
                                contentPanel.add(errorLabel, BorderLayout.CENTER);
                                contentPanel.revalidate();
                                contentPanel.repaint();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            // Show error message
                            JLabel errorLabel = new JLabel("Error fetching card data: " + ex.getMessage());
                            errorLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
                            errorLabel.setForeground(new Color(255, 100, 100));
                            errorLabel.setHorizontalAlignment(JLabel.CENTER);
                            contentPanel.removeAll();
                            contentPanel.add(errorLabel, BorderLayout.CENTER);
                            contentPanel.revalidate();
                            contentPanel.repaint();
                        }
                    }
                };
                worker.execute();
            }
        });

        // Add components to main panel
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createImagePanel(Card card) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(0, 0, 0, 10));
        panel.setPreferredSize(new Dimension(300, 500));

        if (card.getImageInfo() != null && card.getImageInfo().getLarge() != null) {
            // Create a label to hold the image
            JLabel imageLabel = new JLabel();
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            panel.add(imageLabel, BorderLayout.CENTER);

            // Load the image asynchronously with caching
            String imageUrl = card.getImageInfo().getLarge();
            ImageCache.loadImageAsync(imageUrl, -1, 400, new ImageCache.ImageConsumer() {
                @Override
                public void imageLoaded(Image image) {
                    // Update the label with the loaded image
                    imageLabel.setIcon(new ImageIcon(image));
                    panel.revalidate();
                    panel.repaint();
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

    private JPanel createDetailsPanel(Card card) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        // Card name (title)
        JLabel nameLabel = new JLabel(card.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        nameLabel.setForeground(TITLE_COLOR);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(nameLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Card ID
        addDetailRow(panel, "Card ID:", card.getId());

        // Supertype
        addDetailRow(panel, "Supertype:", card.getSupertype());

        // Types
        if (card.getTypes() != null && !card.getTypes().isEmpty()) {
            addDetailRow(panel, "Types:", String.join(", ", card.getTypes()));
        }

        // Subtypes
        if (card.getSubtypes() != null && !card.getSubtypes().isEmpty()) {
            addDetailRow(panel, "Subtypes:", String.join(", ", card.getSubtypes()));
        }

        // Number
        addDetailRow(panel, "Number:", card.getNumber());

        // Rarity
        addDetailRow(panel, "Rarity:", card.getRarity());

        // Set info - show only setId
        if (card.getSetInfo() != null) {
            addDetailRow(panel, "Set:", card.getSetInfo().getSetId());
        }

        // Card market info - show only the BigDecimal value
        if (card.getCardMarket() != null && card.getCardMarket().getPriceInfo() != null 
            && card.getCardMarket().getPriceInfo().getGermanProLow() != null) {
            addDetailRow(panel, "Market Info:", card.getCardMarket().getPriceInfo().getGermanProLow().toString());
        }

        return panel;
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setBackground(BACKGROUND_COLOR);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("SansSerif", Font.BOLD, 14));
        labelComponent.setForeground(TEXT_COLOR);

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("SansSerif", Font.PLAIN, 14));
        valueComponent.setForeground(TEXT_COLOR);

        rowPanel.add(labelComponent, BorderLayout.WEST);
        rowPanel.add(valueComponent, BorderLayout.CENTER);

        panel.add(rowPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private JPanel createSearchByNameScreen() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Search panel at the top
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel searchLabel = new JLabel("Enter Card Name: ");
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchLabel.setForeground(BLACK_TEXT_COLOR);

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchButton.setBackground(Color.WHITE);
        searchButton.setForeground(BLACK_TEXT_COLOR);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(BLACK_TEXT_COLOR);
        backButton.addActionListener(e -> cardLayout.show(cardContainer, "welcome"));

        searchPanel.add(backButton);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchLabel);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchField);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchButton);

        // Results panel (will be populated when search is performed)
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(BACKGROUND_COLOR);

        // Scroll pane for results
        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Initial message
        JLabel initialLabel = new JLabel("Enter a card name and click Search");
        initialLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        initialLabel.setForeground(TEXT_COLOR);
        initialLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultsPanel.add(initialLabel);

        // Add action to the Search button
        searchButton.addActionListener(e -> {
            String cardName = searchField.getText().trim();
            if (cardName.isEmpty()) {
                JOptionPane.showMessageDialog(panel, 
                    "Please enter a card name", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Show loading message
            resultsPanel.removeAll();
            JLabel loadingLabel = new JLabel("Searching for cards...");
            loadingLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
            loadingLabel.setForeground(TEXT_COLOR);
            loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            resultsPanel.add(loadingLabel);
            resultsPanel.revalidate();
            resultsPanel.repaint();

            // Use SwingWorker to fetch card data in background
            SwingWorker<List<Card>, Void> worker = new SwingWorker<List<Card>, Void>() {
                @Override
                protected List<Card> doInBackground() {
                    // Wait for the CompletableFuture to complete and get the result
                    return APIGet.queryCardsByName(cardName).join();
                }

                @Override
                protected void done() {
                    try {
                        List<Card> cards = get();
                        resultsPanel.removeAll();

                        if (cards != null && !cards.isEmpty()) {
                            // Create a JList with LazyListModel for efficient rendering
                            LazyListModel listModel = new LazyListModel(cards, CardGUI.this::createCardListItem, 10);
                            JList<JPanel> cardList = new JList<>(listModel);
                            cardList.setCellRenderer(new DefaultListCellRenderer() {
                                @Override
                                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                                    JPanel panel = (JPanel) value;
                                    // Add spacing between items
                                    panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
                                    return panel;
                                }
                            });
                            cardList.setBackground(BACKGROUND_COLOR);
                            cardList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                            // Add selection listener to show card details when a card is selected
                            cardList.addListSelectionListener(e -> {
                                if (!e.getValueIsAdjusting()) {
                                    int selectedIndex = cardList.getSelectedIndex();
                                    if (selectedIndex >= 0) {
                                        Card selectedCard = cards.get(selectedIndex);

                                        // Create card display panel
                                        JPanel contentPanel = (JPanel) ((JPanel) cardContainer.getComponent(1)).getComponent(1);
                                        contentPanel.removeAll();

                                        // Card display with image and details
                                        JPanel cardDisplayPanel = new JPanel(new BorderLayout(10, 10));
                                        cardDisplayPanel.setBackground(BACKGROUND_COLOR);

                                        // Card image panel (left side)
                                        JPanel fullImagePanel = createImagePanel(selectedCard);
                                        cardDisplayPanel.add(fullImagePanel, BorderLayout.WEST);

                                        // Card details panel (right side)
                                        JPanel fullDetailsPanel = createDetailsPanel(selectedCard);
                                        cardDisplayPanel.add(fullDetailsPanel, BorderLayout.CENTER);

                                        contentPanel.add(cardDisplayPanel, BorderLayout.CENTER);
                                        contentPanel.revalidate();
                                        contentPanel.repaint();

                                        // Switch to the search screen to show full details
                                        cardLayout.show(cardContainer, "search");
                                    }
                                }
                            });

                            // Clear the results panel and add the JList
                            resultsPanel.removeAll();
                            resultsPanel.setLayout(new BorderLayout());
                            resultsPanel.add(cardList, BorderLayout.CENTER);
                        } else {
                            // Show no results message
                            JLabel noResultsLabel = new JLabel("No cards found with that name");
                            noResultsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
                            noResultsLabel.setForeground(TEXT_COLOR);
                            noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                            resultsPanel.add(noResultsLabel);
                        }

                        resultsPanel.revalidate();
                        resultsPanel.repaint();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        // Show error message
                        resultsPanel.removeAll();
                        JLabel errorLabel = new JLabel("Error searching for cards: " + ex.getMessage());
                        errorLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
                        errorLabel.setForeground(new Color(255, 100, 100));
                        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        resultsPanel.add(errorLabel);
                        resultsPanel.revalidate();
                        resultsPanel.repaint();
                    }
                }
            };
            worker.execute();
        });

        // Add components to main panel
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCardListItem(Card card) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Card image (small)
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(PANEL_COLOR);
        imagePanel.setPreferredSize(new Dimension(80, 80));

        if (card.getImageInfo() != null && card.getImageInfo().getSmall() != null) {
            // Create a label to hold the image
            JLabel imageLabel = new JLabel();
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            imagePanel.add(imageLabel, BorderLayout.CENTER);

            // Load the image asynchronously with caching
            String imageUrl = card.getImageInfo().getSmall();
            ImageCache.loadImageAsync(imageUrl, 70, 70, new ImageCache.ImageConsumer() {
                @Override
                public void imageLoaded(Image image) {
                    // Update the label with the loaded image
                    imageLabel.setIcon(new ImageIcon(image));
                    imagePanel.revalidate();
                    imagePanel.repaint();
                }
            });
        } else {
            JLabel noImageLabel = new JLabel("No image");
            noImageLabel.setForeground(TEXT_COLOR);
            noImageLabel.setHorizontalAlignment(JLabel.CENTER);
            imagePanel.add(noImageLabel, BorderLayout.CENTER);
        }

        // Card name and details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(PANEL_COLOR);

        JLabel nameLabel = new JLabel(card.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailsPanel.add(nameLabel);

        // Add set ID if available
        if (card.getSetInfo() != null) {
            JLabel setLabel = new JLabel("Set: " + card.getSetInfo().getSetId());
            setLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            setLabel.setForeground(TEXT_COLOR);
            setLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            detailsPanel.add(setLabel);
        }

        // Make the panel look clickable
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panel.add(imagePanel, BorderLayout.WEST);
        panel.add(detailsPanel, BorderLayout.CENTER);

        return panel;
    }

    public static void displayCard(Card card) {
        SwingUtilities.invokeLater(() -> {
            CardGUI gui = new CardGUI();
            gui.setVisible(true);

            // Create a panel to display the card
            JPanel cardDisplayPanel = new JPanel(new BorderLayout(10, 10));
            cardDisplayPanel.setBackground(BACKGROUND_COLOR);

            // Card image panel (left side)
            JPanel imagePanel = gui.createImagePanel(card);
            cardDisplayPanel.add(imagePanel, BorderLayout.WEST);

            // Card details panel (right side)
            JPanel detailsPanel = gui.createDetailsPanel(card);
            cardDisplayPanel.add(detailsPanel, BorderLayout.CENTER);

            // Get the content panel from the search screen and update it
            JPanel searchPanel = (JPanel) gui.cardContainer.getComponent(1);
            JPanel contentPanel = (JPanel) searchPanel.getComponent(1);
            contentPanel.removeAll();
            contentPanel.add(cardDisplayPanel, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();

            // Switch to the search screen
            gui.cardLayout.show(gui.cardContainer, "search");
        });
    }
}
