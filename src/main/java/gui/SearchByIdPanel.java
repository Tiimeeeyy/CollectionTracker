package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import lombok.Getter;

public class SearchByIdPanel extends JPanel {
    private final CardController controller;
    @Getter private JPanel contentPanel;

    public SearchByIdPanel(CardController controller) {
        this.controller = controller;
        initPanel();
    }

    private void initPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(CardGUI.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CardGUI.BACKGROUND_COLOR);

        JLabel initialLabel = new JLabel("Enter a card ID and click Go to search");
        initialLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        initialLabel.setForeground(CardGUI.TEXT_COLOR);
        initialLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(initialLabel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setBackground(CardGUI.BACKGROUND_COLOR);
        searchPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JButton backButton = UIUtils.createStandardButton("Back");
        backButton.addActionListener(e -> controller.navigateTo("welcome"));

        JLabel searchLabel = new JLabel("Enter Card ID: ");
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchLabel.setForeground(CardGUI.TEXT_COLOR);

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JButton goButton = UIUtils.createStandardButton("Go");
        goButton.addActionListener(e -> controller.searchCardById(searchField.getText().trim(), contentPanel));

        searchPanel.add(backButton);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchLabel);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchField);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(goButton);

        return searchPanel;
    }
}
