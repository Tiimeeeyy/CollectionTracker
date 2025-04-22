package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * JPanel for searching Pokémon cards by their set ID.
 * Follows the design pattern of SearchByNamePanel.
 */
public class SearchBySetPanel extends JPanel {
    private final CardController controller;
    private JPanel resultsPanel; // Panel to display search results
    private JTextField searchField; // Text field for set ID input

    /**
     * Constructor for SearchBySetPanel.
     * @param controller The main CardController for handling actions and navigation.
     */
    public SearchBySetPanel(CardController controller) {
        this.controller = controller;
        initPanel();
    }

    /**
     * Initializes the panel layout and components.
     */
    private void initPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(CardGUI.BACKGROUND_COLOR);
        // Add padding around the panel
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create the top search input panel
        JPanel searchInputPanel = createSearchInputPanel();
        add(searchInputPanel, BorderLayout.NORTH);

        // Create the main content panel for results
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(CardGUI.BACKGROUND_COLOR);

        // Set the initial state message
        UIUtils.setInitialState(resultsPanel, "Enter a Set ID (e.g., 'swsh11', 'base1') and click Search");

        // Add the results panel to a scroll pane
        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBackground(CardGUI.BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // No border for the scroll pane itself
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Improve scroll speed
        scrollPane.getViewport().setBackground(CardGUI.BACKGROUND_COLOR); // Ensure viewport matches theme

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Creates the top panel containing the back button, label, text field, and search button.
     * @return The configured search input JPanel.
     */
    private JPanel createSearchInputPanel() {
        JPanel searchPanel = new JPanel();
        // Use BoxLayout for horizontal arrangement
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setBackground(CardGUI.BACKGROUND_COLOR);
        // Add padding below the search panel
        searchPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Back Button
        JButton backButton = UIUtils.createStandardButton("Back");
        // Add action listener to navigate back to the welcome screen via controller
        backButton.addActionListener(e -> controller.navigateTo("welcome"));

        // Label for the search field
        JLabel searchLabel = new JLabel("Enter Set ID: ");
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchLabel.setForeground(CardGUI.TEXT_COLOR);

        // Text Field for input
        searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); // Limit height
        // Add action listener to trigger search on pressing Enter
        searchField.addActionListener(e -> performSearch());


        // Search Button
        JButton searchButton = UIUtils.createStandardButton("Search");
        // Add action listener to trigger search via controller
        searchButton.addActionListener(e -> performSearch());

        // Add components to the search panel with spacing
        searchPanel.add(backButton);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchLabel);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchField);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchButton);

        return searchPanel;
    }

    /**
     * Helper method to initiate the search process.
     * Retrieves text from the search field and calls the controller.
     */
    private void performSearch() {
        String setId = searchField.getText().trim();
        // Basic validation: ensure input is not empty
        if (setId.isEmpty()) {
            UIUtils.showErrorMessage(this, "Please enter a Set ID.");
            return;
        }
        // Delegate the search action to the controller
        controller.searchCardsBySet(setId, resultsPanel);
    }

    /**
     * Allows external components (like the controller) to get the results panel
     * for updating its content (e.g., showing loading state, results, or errors).
     * @return The JPanel used for displaying results.
     */
    public JPanel getResultsPanel() {
        return resultsPanel;
    }
}

