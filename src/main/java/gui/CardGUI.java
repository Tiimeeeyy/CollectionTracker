package gui;

import api.APIGet;
import data.Card;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

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
        JButton searchButton = new JButton("Start Searching");
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        searchButton.setBackground(Color.WHITE);
        searchButton.setForeground(BLACK_TEXT_COLOR);
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchButton.setMaximumSize(new Dimension(200, 50));
        searchButton.addActionListener(e -> cardLayout.show(cardContainer, "search"));

        // Add components with spacing
        panel.add(Box.createVerticalGlue());
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(descLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 50)));
        panel.add(searchButton);
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
                        return APIGet.getCardDataById(cardId);
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

        try {
            if (card.getImageInfo() != null && card.getImageInfo().getLarge() != null) {
                URL imageUrl = new URL(card.getImageInfo().getLarge());
                BufferedImage image = ImageIO.read(imageUrl);

                // Scale the image to fit the panel while maintaining aspect ratio
                ImageIcon imageIcon = new ImageIcon(image);
                Image scaledImage = imageIcon.getImage().getScaledInstance(
                        -1, 400, Image.SCALE_SMOOTH);

                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                imageLabel.setHorizontalAlignment(JLabel.CENTER);
                panel.add(imageLabel, BorderLayout.CENTER);
            } else {
                JLabel noImageLabel = new JLabel("No image available");
                noImageLabel.setForeground(TEXT_COLOR);
                noImageLabel.setHorizontalAlignment(JLabel.CENTER);
                panel.add(noImageLabel, BorderLayout.CENTER);
            }
        } catch (IOException e) {
            JLabel errorLabel = new JLabel("Error loading image");
            errorLabel.setForeground(TEXT_COLOR);
            errorLabel.setHorizontalAlignment(JLabel.CENTER);
            panel.add(errorLabel, BorderLayout.CENTER);
            e.printStackTrace();
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

        // Set info
        if (card.getSetInfo() != null) {
            addDetailRow(panel, "Set:", card.getSetInfo().toString());
        }

        // Card market info
        if (card.getCardMarket() != null) {
            addDetailRow(panel, "Market Info:", card.getCardMarket().toString());
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
