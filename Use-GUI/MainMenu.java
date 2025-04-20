import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;

public class MainMenu extends JFrame {
    // Custom colors matching GameFrame
    private final Color BACKGROUND_COLOR = new Color(32, 33, 36);  // Dark background
    private final Color ACCENT_COLOR = new Color(138, 180, 248);   // Blue accent
    private final Color TEXT_COLOR = new Color(248, 249, 250);     // Light text
    
    // Background image
    private Image backgroundImage;
    private boolean hasBackgroundImage = false;
    
    // Custom fonts
    private Font gameFont;
    private Font titleFont;
    private Font buttonFont;

    public MainMenu() {
        // Load custom resources
        loadResources();
        
        setTitle("Word Trivia - Main Menu");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        
        // Use custom panel with background image
        JPanel mainPanel = new BackgroundPanel();
        mainPanel.setLayout(new GridBagLayout());
        setContentPane(mainPanel);

        // Title Label with improved styling
        JLabel title = new JLabel("Word Trivia Game", SwingConstants.CENTER);
        title.setFont(titleFont);
        title.setForeground(TEXT_COLOR);

        // Card-like panel for buttons with enhanced styling
        JPanel cardPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        cardPanel.setOpaque(false);
        
        JButton gameButton = createStyledButton("Game Phase");
        JButton adminButton = createStyledButton("Admin Phase");
        JButton exitButton = createStyledButton("Exit");

        cardPanel.add(gameButton);
        cardPanel.add(adminButton);
        cardPanel.add(exitButton);

        // Rounded border for card effect
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 2, true),
                BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));

        // Layout positioning
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(40, 0, 30, 0);
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

    private void loadResources() {
        // Load custom font
        try {
            // Try to load Roboto font from Google
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, 
                new URL("https://github.com/googlefonts/roboto/raw/main/src/hinted/Roboto-Regular.ttf").openStream());
            gameFont = baseFont.deriveFont(Font.PLAIN, 16f);
            titleFont = baseFont.deriveFont(Font.BOLD, 36f);
            buttonFont = baseFont.deriveFont(Font.BOLD, 20f);

            // Register font
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(baseFont);
        } catch (Exception e) {
            // Fallback fonts if custom font fails to load
            gameFont = new Font("SansSerif", Font.PLAIN, 16);
            titleFont = new Font("SansSerif", Font.BOLD, 36);
            buttonFont = new Font("SansSerif", Font.BOLD, 20);
            System.out.println("Custom font could not be loaded: " + e.getMessage());
        }

        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("lights.jpg"));
            hasBackgroundImage = true;
        } catch (Exception e) {
            System.out.println("Background image could not be loaded: " + e.getMessage());
            hasBackgroundImage = false;
        }
    }

    // Custom panel class for background image
    class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (hasBackgroundImage) {
                // Draw background image with opacity
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2d.dispose();
            }

            // Draw gradient overlay
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setPaint(new GradientPaint(0, 0, new Color(0, 0, 0, 220), 
                                         0, getHeight(), new Color(0, 0, 0, 200)));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(ACCENT_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(ACCENT_COLOR.brighter());
                } else {
                    g2.setColor(ACCENT_COLOR);
                }

                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));

                g2.setColor(TEXT_COLOR);
                g2.setFont(buttonFont);
                
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);

                g2.dispose();
            }
        };

        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(buttonFont);
        button.setForeground(TEXT_COLOR);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 60));

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
    }
}
