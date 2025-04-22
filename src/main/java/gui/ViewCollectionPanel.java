package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * JPanel for displaying the user's saved Pokémon card collection from the database.
 */
public class ViewCollectionPanel extends JPanel {
    private final CardController controller;
    private JPanel collectionPanel; // Panel to display the collection items

    /**
     * Constructor for ViewCollectionPanel.
     * @param controller The main CardController for handling actions and navigation.
     */
    public ViewCollectionPanel(CardController controller) {
        this.controller = controller;
        initPanel();
    }

    /**
     * Initializes the panel layout and components.
     * This panel is simpler as it doesn't have search input fields.
     */
    private void initPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(CardGUI.BACKGROUND_COLOR);
        // Add padding around the panel
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create the top panel with Back button and Title
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Create the main content panel for the collection list
        collectionPanel = new JPanel();
        collectionPanel.setLayout(new BoxLayout(collectionPanel, BoxLayout.Y_AXIS));
        collectionPanel.setBackground(CardGUI.BACKGROUND_COLOR);

        // Set the initial state (will be quickly replaced by loading/results)
        UIUtils.setInitialState(collectionPanel, "Loading collection...");

        // Add the collection panel to a scroll pane
        JScrollPane scrollPane = new JScrollPane(collectionPanel);
        scrollPane.setBackground(CardGUI.BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(CardGUI.BACKGROUND_COLOR);

        add(scrollPane, BorderLayout.CENTER);

        // Add a component listener to trigger loading when the panel becomes visible
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                // Tell the controller to load the collection data into the panel
                controller.loadCollectionView(collectionPanel);
            }
        });
    }

    /**
     * Creates the top panel containing the back button and title.
     * @return The configured top JPanel.
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 0)); // Use BorderLayout for simple structure
        topPanel.setBackground(CardGUI.BACKGROUND_COLOR);
        // Add padding below the top panel
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Back Button
        JButton backButton = UIUtils.createStandardButton("Back");
        backButton.addActionListener(e -> controller.navigateTo("welcome"));

        // Title Label
        JLabel titleLabel = UIUtils.createSectionHeader("My Collection"); // Use section header style
        titleLabel.setHorizontalAlignment(JLabel.CENTER); // Center the title

        // Add components to the top panel
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        return topPanel;
    }

    /**
     * Allows external components (like the controller) to get the collection panel
     * for updating its content (e.g., showing loading state, results, or errors).
     * @return The JPanel used for displaying the collection.
     */
    public JPanel getCollectionPanel() {
        return collectionPanel;
    }
}
