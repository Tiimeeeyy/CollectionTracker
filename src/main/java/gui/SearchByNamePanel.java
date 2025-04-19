package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SearchByNamePanel extends JPanel {
    private final CardController controller;
    private JPanel resultsPanel;

    public SearchByNamePanel(CardController controller) {
        this.controller = controller;
        initPanel();
    }

    private void initPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(CardGUI.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);

        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(CardGUI.BACKGROUND_COLOR);

        UIUtils.setInitialState(resultsPanel, "Enter a card name and click Search");

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBackground(CardGUI.BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(CardGUI.BACKGROUND_COLOR);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setBackground(CardGUI.BACKGROUND_COLOR);
        searchPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JButton backButton = UIUtils.createStandardButton("Back");
        backButton.addActionListener(e -> controller.navigateTo("welcome"));

        JLabel searchLabel = new JLabel("Enter Card Name: ");
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchLabel.setForeground(CardGUI.TEXT_COLOR);

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JButton searchButton = UIUtils.createStandardButton("Search");
        searchButton.addActionListener(e -> controller.searchCardsByName(searchField.getText().trim(), resultsPanel));

        searchPanel.add(backButton);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchLabel);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchField);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchButton);

        return searchPanel;
    }
}
