package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * The initial welcome screen panel for the application.
 * Provides navigation buttons to different features.
 */
public class WelcomePanel extends JPanel {
    private final CardController controller;

    /**
     * Constructor for WelcomePanel.
     * @param controller The main CardController for handling navigation actions.
     */
    public WelcomePanel(CardController controller) {
        this.controller = controller;
        initPanel();
    }

    /**
     * Initializes the panel layout and components.
     */
    private void initPanel() {
        // Use BoxLayout for vertical stacking of components
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(CardGUI.BACKGROUND_COLOR);
        // Add padding around the content
        setBorder(new EmptyBorder(50, 50, 50, 50));

        // Add flexible space at the top to push content towards center
        add(Box.createVerticalGlue());

        // Title Label
        add(UIUtils.createTitleLabel("Welcome to Pokemon Card Collection Tracker"));
        add(Box.createRigidArea(new Dimension(0, 20))); // Spacing

        // Subtitle Label
        add(UIUtils.createSubtitleLabel("Search, view, and track your Pokemon cards"));
        add(Box.createRigidArea(new Dimension(0, 50))); // Spacing

        // --- Navigation Buttons ---
        int buttonWidth = 220; // Define a consistent width for buttons
        int buttonHeight = 40; // Define a consistent height for buttons
        Dimension buttonSize = new Dimension(buttonWidth, buttonHeight);

        // Search by ID Button
        JButton searchByIdButton = UIUtils.createStandardButton("Search by Card ID");
        configureNavButton(searchByIdButton, buttonSize, "search");
        add(searchByIdButton);
        add(Box.createRigidArea(new Dimension(0, 15))); // Spacing between buttons

        // Search by Name Button
        JButton searchByNameButton = UIUtils.createStandardButton("Search by Card Name");
        configureNavButton(searchByNameButton, buttonSize, "searchByName");
        add(searchByNameButton);
        add(Box.createRigidArea(new Dimension(0, 15))); // Spacing

        // Search by Set Button (New)
        JButton searchBySetButton = UIUtils.createStandardButton("Search by Set");
        configureNavButton(searchBySetButton, buttonSize, "searchBySet");
        add(searchBySetButton);
        add(Box.createRigidArea(new Dimension(0, 15))); // Spacing

        // Search by Pokédex Button (New)
        JButton searchByPokedexButton = UIUtils.createStandardButton("Search by Pokédex");
        configureNavButton(searchByPokedexButton, buttonSize, "searchByPokedex");
        add(searchByPokedexButton);
        add(Box.createRigidArea(new Dimension(0, 15))); // Spacing

        // View Collection Button (New)
        JButton viewCollectionButton = UIUtils.createStandardButton("View My Collection");
        configureNavButton(viewCollectionButton, buttonSize, "viewCollection");
        add(viewCollectionButton);

        // Add flexible space at the bottom
        add(Box.createVerticalGlue());
    }

    /**
     * Helper method to configure common properties for navigation buttons.
     * @param button The JButton to configure.
     * @param size The preferred/max size for the button.
     * @param screenName The screen name to navigate to via the controller.
     */
    private void configureNavButton(JButton button, Dimension size, String screenName) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button horizontally
        button.setPreferredSize(size);
        button.setMaximumSize(size); // Prevent stretching
        // Add action listener to navigate using the controller
        button.addActionListener(e -> controller.navigateTo(screenName));
    }
}

