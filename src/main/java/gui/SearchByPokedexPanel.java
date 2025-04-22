package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * JPanel for searching Pokémon cards by their National Pokédex number range.
 */
public class SearchByPokedexPanel extends JPanel {
    private final CardController controller;
    private JPanel resultsPanel; // Panel to display search results
    private JTextField startRangeField; // Text field for the start of the range
    private JTextField endRangeField; // Text field for the end of the range

    /**
     * Constructor for SearchByPokedexPanel.
     * @param controller The main CardController for handling actions and navigation.
     */
    public SearchByPokedexPanel(CardController controller) {
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
        UIUtils.setInitialState(resultsPanel, "Enter Pokédex number range and click Search");

        // Add the results panel to a scroll pane
        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBackground(CardGUI.BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(CardGUI.BACKGROUND_COLOR);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Creates the top panel containing the back button, labels, text fields for range, and search button.
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
        backButton.addActionListener(e -> controller.navigateTo("welcome"));

        // Label for the range input
        JLabel rangeLabel = new JLabel("Pokédex Range: ");
        rangeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        rangeLabel.setForeground(CardGUI.TEXT_COLOR);

        // Text Field for start range
        startRangeField = new JTextField();
        startRangeField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        // Set preferred and maximum size for a smaller text field
        Dimension rangeFieldSize = new Dimension(60, 30);
        startRangeField.setPreferredSize(rangeFieldSize);
        startRangeField.setMaximumSize(rangeFieldSize);
        // Add action listener to trigger search on pressing Enter in either field
        startRangeField.addActionListener(e -> performSearch());


        // Label between the range fields
        JLabel toLabel = new JLabel(" to ");
        toLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        toLabel.setForeground(CardGUI.TEXT_COLOR);

        // Text Field for end range
        endRangeField = new JTextField();
        endRangeField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        endRangeField.setPreferredSize(rangeFieldSize);
        endRangeField.setMaximumSize(rangeFieldSize);
        endRangeField.addActionListener(e -> performSearch());

        // Search Button
        JButton searchButton = UIUtils.createStandardButton("Search");
        searchButton.addActionListener(e -> performSearch());

        // Add components to the search panel with spacing
        searchPanel.add(backButton);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(rangeLabel);
        searchPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        searchPanel.add(startRangeField);
        searchPanel.add(toLabel);
        searchPanel.add(endRangeField);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchButton);
        searchPanel.add(Box.createHorizontalGlue()); // Pushes components left

        return searchPanel;
    }

    /**
     * Helper method to initiate the search process.
     * Retrieves text from the range fields, performs basic validation, and calls the controller.
     */
    private void performSearch() {
        String startStr = startRangeField.getText().trim();
        String endStr = endRangeField.getText().trim();

        // Basic validation
        if (startStr.isEmpty()) {
            UIUtils.showErrorMessage(this, "Please enter a starting Pokédex number.");
            return;
        }

        // Delegate the search action to the controller, which will handle parsing and validation
        controller.searchCardsByPokedex(startStr, endStr, resultsPanel);
    }

    /**
     * Allows external components to get the results panel for updating its content.
     * @return The JPanel used for displaying results.
     */
    public JPanel getResultsPanel() {
        return resultsPanel;
    }
}
