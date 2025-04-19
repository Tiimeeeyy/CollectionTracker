package gui;

import data.Card;
import util.ImageCache;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CardComponentFactory {
    // Creates the panel containing image, details, and collected checkbox
    public static JPanel createCardDisplayPanel(Card card, CardController controller) {
        JPanel cardDisplayPanel = new JPanel(new BorderLayout(10, 10));
        cardDisplayPanel.setBackground(CardGUI.BACKGROUND_COLOR);
        cardDisplayPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Card image panel (left side)
        JPanel imagePanel = createImagePanel(card);
        cardDisplayPanel.add(imagePanel, BorderLayout.WEST);

        // Card details panel (right side)
        JPanel detailsPanel = createDetailsPanel(card, controller);

        JScrollPane detailsScrollPane = new JScrollPane(detailsPanel);
        detailsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        detailsScrollPane.setBackground(CardGUI.BACKGROUND_COLOR);
        detailsScrollPane.getViewport().setBackground(CardGUI.BACKGROUND_COLOR);
        detailsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        detailsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        cardDisplayPanel.add(detailsScrollPane, BorderLayout.CENTER);

        return cardDisplayPanel;
    }

    // Creates only the image part of the display
    private static JPanel createImagePanel(Card card) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CardGUI.BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(0, 0, 0, 10));
        panel.setPreferredSize(new Dimension(300, 450));

        if (card != null && card.getImageInfo() != null && card.getImageInfo().getLarge() != null) {
            JLabel imageLabel = new JLabel();
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            panel.add(imageLabel, BorderLayout.CENTER);

            String imageUrl = card.getImageInfo().getLarge();
            ImageCache.loadImageAsync(imageUrl, -1, 420, image -> {
                if (image != null) {
                    imageLabel.setIcon(new ImageIcon(image));
                    panel.revalidate();
                    panel.repaint();
                } else {
                    imageLabel.setText("Image load error");
                    imageLabel.setForeground(Color.RED);
                }
            });
        } else {
            JLabel noImageLabel = new JLabel("No image available");
            noImageLabel.setForeground(CardGUI.TEXT_COLOR);
            noImageLabel.setHorizontalAlignment(JLabel.CENTER);
            panel.add(noImageLabel, BorderLayout.CENTER);
        }

        return panel;
    }

    // Creates the details part + collected checkbox
    private static JPanel createDetailsPanel(Card card, CardController controller) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CardGUI.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));

        if (card == null) {
            panel.add(new JLabel("Error: No card data to display."));
            return panel;
        }

        // Card name (title)
        JLabel nameLabel = new JLabel(card.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        nameLabel.setForeground(CardGUI.TITLE_COLOR);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(nameLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Card Details
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

        // Card market info
        if (card.getCardMarket() != null && card.getCardMarket().getPriceInfo() != null
                && card.getCardMarket().getPriceInfo().getAverageSellPrice() != null) {
            addDetailRow(panel, "Market Price (Low):", "€" + card.getCardMarket().getPriceInfo().getAverageSellPrice().toString());
        } else {
            addDetailRow(panel, "Market Price:", "N/A");
        }

        // Add Collected Checkbox
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JCheckBox collectedCheckBox = new JCheckBox("Collected");
        collectedCheckBox.setFont(new Font("SansSerif", Font.BOLD, 14));
        collectedCheckBox.setForeground(CardGUI.TEXT_COLOR);
        collectedCheckBox.setBackground(CardGUI.BACKGROUND_COLOR);
        collectedCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        collectedCheckBox.setSelected(controller.isCardInCollection(card.getId()));

        collectedCheckBox.addActionListener(e -> controller.toggleCardCollection(card, collectedCheckBox));

        panel.add(collectedCheckBox);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // Helper to add a label-value pair row
    private static void addDetailRow(JPanel panel, String label, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        JPanel rowPanel = new JPanel(new BorderLayout(5, 0));
        rowPanel.setBackground(CardGUI.BACKGROUND_COLOR);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("SansSerif", Font.BOLD, 12));
        labelComponent.setForeground(CardGUI.TEXT_COLOR);
        labelComponent.setPreferredSize(new Dimension(80, 20));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("SansSerif", Font.PLAIN, 12));
        valueComponent.setForeground(CardGUI.TEXT_COLOR);
        valueComponent.setPreferredSize(new Dimension(100, 20));

        rowPanel.add(labelComponent, BorderLayout.WEST);
        rowPanel.add(valueComponent, BorderLayout.CENTER);

        panel.add(rowPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 3)));
    }

    // Creates a JPanel representing a single card in the search-by-name list
    public static JPanel createCardListItem(Card card) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(CardGUI.PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, CardGUI.BACKGROUND_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10))
        );
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Card image (small)
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(CardGUI.PANEL_COLOR);
        imagePanel.setPreferredSize(new Dimension(60, 80));

        if (card.getImageInfo() != null && card.getImageInfo().getSmall() != null) {
            JLabel imageLabel = new JLabel();
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            imagePanel.add(imageLabel, BorderLayout.CENTER);

            String imageUrl = card.getImageInfo().getSmall();
            ImageCache.loadImageAsync(imageUrl, 60, -1, image -> {
                if (image != null) {
                    imageLabel.setIcon(new ImageIcon(image));
                    imagePanel.revalidate();
                    imagePanel.repaint();
                } else {
                    imageLabel.setText("N/A");
                    imageLabel.setForeground(CardGUI.TEXT_COLOR);
                }
            });
        } else {
            JLabel noImageLabel = new JLabel("No Img");
            noImageLabel.setForeground(CardGUI.TEXT_COLOR);
            noImageLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
            noImageLabel.setHorizontalAlignment(JLabel.CENTER);
            imagePanel.add(noImageLabel, BorderLayout.CENTER);
        }

        // Card name and details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(CardGUI.PANEL_COLOR);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JLabel nameLabel = new JLabel(card.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setForeground(CardGUI.TEXT_COLOR);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(nameLabel);

        // Add set name and rarity
        String setInfo = "Set: " + (card.getSetInfo() != null ? card.getSetInfo().getSetName() : "N/A");
        String rarityInfo = "Rarity: " + (card.getRarity() != null ? card.getRarity() : "N/A");

        JLabel setLabel = new JLabel(setInfo);
        setLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        setLabel.setForeground(CardGUI.TEXT_COLOR);
        setLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rarityLabel = new JLabel(rarityInfo);
        rarityLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        rarityLabel.setForeground(CardGUI.TEXT_COLOR);
        rarityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        detailsPanel.add(setLabel);
        detailsPanel.add(rarityLabel);

        panel.add(imagePanel, BorderLayout.WEST);
        panel.add(detailsPanel, BorderLayout.CENTER);

        return panel;
    }
}
