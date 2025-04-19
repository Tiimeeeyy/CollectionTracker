package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

public class UIUtils {

    // Common methods to create styled components
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(CardGUI.PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        return button;
    }

    public static JButton createStandardButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        // Use system UI defaults for colors
        return button;
    }

    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(CardGUI.PANEL_COLOR);
        button.setForeground(CardGUI.TEXT_COLOR);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return button;
    }

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 24));
        label.setForeground(CardGUI.TITLE_COLOR);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setForeground(CardGUI.TEXT_COLOR);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    public static JPanel createRoundedPanel(Color bgColor) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBackground(bgColor);
        return panel;
    }

    // Add hover effect to any component
    public static void addHoverEffect(JComponent component, Color defaultColor, Color hoverColor) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                component.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                component.setBackground(defaultColor);
            }
        });
    }

    // Create standard section headers
    public static JLabel createSectionHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        label.setForeground(CardGUI.TITLE_COLOR);
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return label;
    }

    // Create a search field
    public static JTextField createSearchField(String placeholder, Consumer<String> searchAction) {
        JTextField searchField = new JTextField(placeholder);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);

        // Add placeholder behavior
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals(placeholder)) {
                    searchField.setText("");
                    searchField.setForeground(CardGUI.TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText(placeholder);
                }
            }
        });

        // Add action listener
        searchField.addActionListener(e -> searchAction.accept(searchField.getText()));

        return searchField;
    }

    // Display loading indicator
    public static JPanel createLoadingPanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CardGUI.BACKGROUND_COLOR);

        JLabel loadingLabel = new JLabel(message);
        loadingLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        loadingLabel.setForeground(CardGUI.TEXT_COLOR);
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        panel.add(loadingLabel, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);

        return panel;
    }

    // Show error dialog with custom styling
    public static void showErrorDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    // Format card price for display
    public static String formatPrice(Double price) {
        if (price == null) {
            return "N/A";
        }
        return String.format("€%.2f", price);
    }

    // Methods referenced in other classes but missing in UIUtils
    public static void showErrorMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public static void setLoadingState(JPanel panel, String message) {
        setPanelState(panel, message, CardGUI.TEXT_COLOR);
    }

    public static void setErrorState(JPanel panel, String message) {
        setPanelState(panel, message, new Color(255, 100, 100)); // Red for errors
    }

    public static void setInitialState(JPanel panel, String message) {
        setPanelState(panel, message, CardGUI.TEXT_COLOR);
    }

    public static void updateContentPanel(JPanel targetPanel, JPanel newContent) {
        targetPanel.removeAll();
        targetPanel.setLayout(new BorderLayout()); // Ensure layout manager
        targetPanel.add(newContent, BorderLayout.CENTER);
        targetPanel.revalidate();
        targetPanel.repaint();
    }

    // Helper method for panel states
    private static void setPanelState(JPanel targetPanel, String message, Color textColor) {
        targetPanel.removeAll();
        targetPanel.setLayout(new BorderLayout()); // Ensure BorderLayout for centering
        JLabel label = new JLabel(message);
        label.setFont(new Font("SansSerif", Font.ITALIC, 16));
        label.setForeground(textColor);
        label.setHorizontalAlignment(JLabel.CENTER);
        targetPanel.add(label, BorderLayout.CENTER);
        targetPanel.revalidate();
        targetPanel.repaint();
    }
}