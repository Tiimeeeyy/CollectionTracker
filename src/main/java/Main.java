import config.AppConfig;
import database.CardRepository;
import gui.CardGUI;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Starting application...");
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

            // Register the configuration class
            context.register(AppConfig.class);

            // Refresh context with detailed error handling
            try {
                context.refresh();
                System.out.println("Spring context initialized successfully");
            } catch (Exception e) {
                System.err.println("Failed to initialize Spring context: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }

            // Get repository bean with error handling
            CardRepository cardRepository;
            try {
                cardRepository = context.getBean(CardRepository.class);
                System.out.println("CardRepository bean retrieved successfully");
            } catch (Exception e) {
                System.err.println("Failed to get CardRepository bean: " + e.getMessage());
                e.printStackTrace();
                context.close();
                System.exit(1);
                return;
            }

            // Start the GUI with SwingUtilities
            SwingUtilities.invokeLater(() -> {
                try {
                    CardGUI gui = new CardGUI(cardRepository);
                    gui.setVisible(true);
                    System.out.println("GUI initialized successfully");
                } catch (Exception e) {
                    System.err.println("Failed to initialize GUI: " + e.getMessage());
                    e.printStackTrace();
                }
            });

            // Add a shutdown hook to close the context
            Runtime.getRuntime().addShutdownHook(new Thread(context::close));

        } catch (Exception e) {
            System.err.println("Unhandled exception in main: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}