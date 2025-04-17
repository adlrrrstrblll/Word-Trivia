import java.awt.*;
import javax.swing.*;

public class MainMenu extends JFrame {

    public MainMenu() {
        setTitle("Word Trivia - Main Menu");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        // Set background color
        getContentPane().setBackground(new Color(245, 245, 255)); // soft light blue

        // Title Label
        JLabel title = new JLabel("Word Trivia Game", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 42));
        title.setForeground(new Color(44, 62, 80)); // dark slate

        // Card-like panel for buttons
        JPanel cardPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setOpaque(true);

        JButton gameButton = createStyledButton("Game Phase");
        JButton adminButton = createStyledButton("Admin Phase");
        JButton exitButton = createStyledButton("Exit");

        cardPanel.add(gameButton);
        cardPanel.add(adminButton);
        cardPanel.add(exitButton);

        // Rounded border for card effect
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 220), 2),
                BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));

        // Layout positioning
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(30, 0, 30, 0);
        add(title, gbc);

        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        add(cardPanel, gbc);

        // Button actions
        gameButton.addActionListener(e -> {
            new GameFrame().setVisible(true);
            dispose();
        });

        adminButton.addActionListener(e -> {
            AdminLogic adminLogic = new AdminLogic();
            new AdminFrame(adminLogic).setVisible(true);
            dispose();
        });

        exitButton.addActionListener(e -> System.exit(0));
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 20));
        button.setBackground(new Color(52, 152, 219)); // blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
    }
}
