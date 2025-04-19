package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class WelcomePanel extends JPanel {
    private final CardController controller;

    public WelcomePanel(CardController controller) {
        this.controller = controller;
        initPanel();
    }

    private void initPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(CardGUI.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(50, 50, 50, 50));

        add(Box.createVerticalGlue());
        add(UIUtils.createTitleLabel("Welcome to Pokemon Card Collection Tracker"));
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(UIUtils.createSubtitleLabel("Search and view information about Pokemon cards"));
        add(Box.createRigidArea(new Dimension(0, 50)));

        JButton searchButton = UIUtils.createStandardButton("Search by ID");
        searchButton.addActionListener(e -> controller.navigateTo("search"));
        add(searchButton);

        add(Box.createRigidArea(new Dimension(0, 20)));

        JButton searchByNameButton = UIUtils.createStandardButton("Search by Name");
        searchByNameButton.addActionListener(e -> controller.navigateTo("searchByName"));
        add(searchByNameButton);

        add(Box.createVerticalGlue());
    }
}
