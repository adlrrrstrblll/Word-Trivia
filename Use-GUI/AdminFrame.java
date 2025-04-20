import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;

public class AdminFrame extends JFrame {
    private JTextArea displayArea;
    private JPanel buttonPanel;
    private AdminLogic adminLogic;

    // Custom colors (matching GameFrame)
    private final Color BACKGROUND_COLOR = new Color(32, 33, 36);  // Dark background
    private final Color ACCENT_COLOR = new Color(138, 180, 248);   // Blue accent
    private final Color TEXT_COLOR = new Color(248, 249, 250);     // Light text
    private final Color PANEL_COLOR = new Color(42, 43, 46);      // Slightly lighter than background

    // Background image
    private Image backgroundImage;
    private boolean hasBackgroundImage = false;

    // Custom fonts
    private Font gameFont;
    private Font titleFont;
    private Font buttonFont;

    public AdminFrame(AdminLogic adminLogic) {
        this.adminLogic = adminLogic;
        
        // Load custom resources
        loadResources();

        setTitle("Word Trivia - Admin Panel");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use custom panel with background image
        JPanel mainPanel = new BackgroundPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Header panel with title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Admin Control Panel", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Button panel with styled buttons
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1, 12, 12));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(ACCENT_COLOR, 2, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        String[] options = {
            "Add Word",
            "Add Trivia",
            "Modify Word",
            "Modify Trivia",
            "Delete Word",
            "Delete Trivia",
            "View Words",
            "View Trivia",
            "Export Data",
            "Import Data",
            "Back to Main Menu"
        };

        for (String label : options) {
            JButton btn = createStyledButton(label);
            btn.addActionListener(new AdminButtonListener(label));
            buttonPanel.add(btn);
        }

        // Display area with styled text area
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setOpaque(false);
        displayPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(ACCENT_COLOR, 2, true),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(gameFont);
        displayArea.setForeground(TEXT_COLOR);
        displayArea.setBackground(new Color(45, 46, 48));
        displayArea.setCaretColor(TEXT_COLOR);
        displayArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        displayArea.setLineWrap(true);
        displayArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        displayPanel.add(scrollPane, BorderLayout.CENTER);

        // Layout side by side
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.3;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 10);
        contentPanel.add(buttonPanel, gbc);
        
        gbc.weightx = 0.7;
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 10, 0, 0);
        contentPanel.add(displayPanel, gbc);

        add(contentPanel, BorderLayout.CENTER);
        
        // Status bar at the bottom
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setOpaque(false);
        JLabel statusLabel = new JLabel("Ready. Select an option from the menu.");
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setFont(gameFont.deriveFont(Font.ITALIC));
        statusBar.add(statusLabel, BorderLayout.WEST);
        add(statusBar, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private void loadResources() {
        // Load custom font
        try {
            // Try to load Roboto font from Google
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, 
                new URL("https://github.com/googlefonts/roboto/raw/main/src/hinted/Roboto-Regular.ttf").openStream());
            gameFont = baseFont.deriveFont(Font.PLAIN, 16f);
            titleFont = baseFont.deriveFont(Font.BOLD, 28f);
            buttonFont = baseFont.deriveFont(Font.BOLD, 16f);

            // Register font
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(baseFont);
        } catch (Exception e) {
            // Fallback fonts if custom font fails to load
            gameFont = new Font("SansSerif", Font.PLAIN, 16);
            titleFont = new Font("SansSerif", Font.BOLD, 28);
            buttonFont = new Font("SansSerif", Font.BOLD, 16);
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
        button.setPreferredSize(new Dimension(180, 45));

        return button;
    }

    class AdminButtonListener implements ActionListener {
        private String label;

        AdminButtonListener(String label) {
            this.label = label;
        }

        public void actionPerformed(ActionEvent e) {
            switch (label) {
                case "Add Word":
                    String word = showStyledInputDialog("Enter new word:", "Add Word");
                    if (word != null && !word.isBlank()) {
                        adminLogic.addWordFromGUI(word, AdminFrame.this);
                    }
                    break;
                case "Add Trivia":
                    String wordForTrivia = showStyledInputDialog("Enter word to add trivia to:", "Add Trivia");
                    if (wordForTrivia != null && !wordForTrivia.isBlank()) {
                        String trivia = showStyledInputDialog("Enter trivia for " + wordForTrivia + ":", "Add Trivia");
                        if (trivia != null && !trivia.isBlank()) {
                            adminLogic.addTriviaFromGUI(wordForTrivia, trivia, AdminFrame.this);
                        }
                    }
                    break;
                case "Modify Word":
                    String oldWord = showStyledInputDialog("Enter the word to modify:", "Modify Word");
                    if (oldWord != null && !oldWord.isBlank()) {
                        String newWord = showStyledInputDialog("Enter the new word:", "Modify Word");
                        if (newWord != null && !newWord.isBlank()) {
                            adminLogic.modifyWordFromGUI(oldWord, newWord, AdminFrame.this);
                        }
                    }
                    break;
                case "Modify Trivia":
                    String wordForTriviaModification = showStyledInputDialog("Enter the word to modify trivia for:", "Modify Trivia");
                    if (wordForTriviaModification != null && !wordForTriviaModification.isBlank()) {
                        int index = adminLogic.findWord(wordForTriviaModification);
                        if (index == -1) {
                            showStyledMessageDialog("Word not found.", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        }
                
                        StringBuilder triviaList = new StringBuilder("Select the trivia to modify:\n");
                        for (int i = 0; i < adminLogic.getTriviaCount(index); i++) {
                            triviaList.append((i + 1)).append(". ")
                                      .append(adminLogic.getTriviaRel(index, i))
                                      .append(": ")
                                      .append(adminLogic.getTriviaVal(index, i))
                                      .append("\n");
                        }
                
                        String triviaIndexStr = showStyledInputDialog(triviaList.toString(), "Select Trivia");
                        if (triviaIndexStr != null && !triviaIndexStr.isBlank()) {
                            try {
                                int triviaIndex = Integer.parseInt(triviaIndexStr) - 1;
                                if (triviaIndex < 0 || triviaIndex >= adminLogic.getTriviaCount(index)) {
                                    showStyledMessageDialog("Invalid trivia selection.", "Error", JOptionPane.ERROR_MESSAGE);
                                    break;
                                }
                
                                String newTrivia = showStyledInputDialog("Enter the new trivia (format: relation:value):", "Modify Trivia");
                                if (newTrivia != null && !newTrivia.isBlank()) {
                                    adminLogic.modifyTriviaFromGUI(wordForTriviaModification, triviaIndex, newTrivia, AdminFrame.this);
                                }
                            } catch (NumberFormatException ex) {
                                showStyledMessageDialog("Invalid input. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                    break;
                case "Delete Word":
                    String wordToDelete = showStyledInputDialog("Enter the word to delete:", "Delete Word");
                    if (wordToDelete != null && !wordToDelete.isBlank()) {
                        int confirm = JOptionPane.showConfirmDialog(
                            AdminFrame.this, 
                            "Are you sure you want to delete '" + wordToDelete + "'?", 
                            "Confirm Deletion", 
                            JOptionPane.YES_NO_OPTION, 
                            JOptionPane.WARNING_MESSAGE);
                        
                        if (confirm == JOptionPane.YES_OPTION) {
                            adminLogic.deleteWordFromGUI(wordToDelete, AdminFrame.this);
                        }
                    }
                    break;
                case "Delete Trivia":
                    String wordForTriviaDeletion = showStyledInputDialog("Enter the word to delete trivia from:", "Delete Trivia");
                    if (wordForTriviaDeletion != null && !wordForTriviaDeletion.isBlank()) {
                        int index = adminLogic.findWord(wordForTriviaDeletion);
                        if (index == -1) {
                            showStyledMessageDialog("Word not found.", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        }
                
                        StringBuilder triviaList = new StringBuilder("Select the trivia to delete:\n");
                        for (int i = 0; i < adminLogic.getTriviaCount(index); i++) {
                            triviaList.append((i + 1)).append(". ")
                                      .append(adminLogic.getTriviaRel(index, i))
                                      .append(": ")
                                      .append(adminLogic.getTriviaVal(index, i))
                                      .append("\n");
                        }
                
                        String triviaIndexStr = showStyledInputDialog(triviaList.toString(), "Select Trivia");
                        if (triviaIndexStr != null && !triviaIndexStr.isBlank()) {
                            try {
                                int triviaIndex = Integer.parseInt(triviaIndexStr) - 1;
                                if (triviaIndex < 0 || triviaIndex >= adminLogic.getTriviaCount(index)) {
                                    showStyledMessageDialog("Invalid trivia selection.", "Error", JOptionPane.ERROR_MESSAGE);
                                    break;
                                }
                
                                int confirm = JOptionPane.showConfirmDialog(
                                    AdminFrame.this, 
                                    "Are you sure you want to delete this trivia?", 
                                    "Confirm Deletion", 
                                    JOptionPane.YES_NO_OPTION, 
                                    JOptionPane.WARNING_MESSAGE);
                                
                                if (confirm == JOptionPane.YES_OPTION) {
                                    String triviaRelation = adminLogic.getTriviaRel(index, triviaIndex);
                                    String triviaValue = adminLogic.getTriviaVal(index, triviaIndex);
                                    String triviaString = triviaRelation + ":" + triviaValue;
                                    
                                    adminLogic.deleteTriviaFromGUI(wordForTriviaDeletion, triviaString, AdminFrame.this);
                                }
                            } catch (NumberFormatException ex) {
                                showStyledMessageDialog("Invalid input. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                    break;
                case "View Words":
                    displayArea.setText(adminLogic.viewWords());
                    break;
                case "View Trivia":
                    displayArea.setText(adminLogic.viewTrivia());
                    break;
                case "Export Data":
                    String exportFilename = showStyledInputDialog("Enter filename to export data:", "Export Data");
                    if (exportFilename != null && !exportFilename.isBlank()) {
                        adminLogic.exportData(exportFilename, AdminFrame.this);
                    }
                    break;
                case "Import Data":
                    String importFilename = showStyledInputDialog("Enter filename to import data:", "Import Data");
                    if (importFilename != null && !importFilename.isBlank()) {
                        adminLogic.importData(importFilename, AdminFrame.this);
                    }
                    break;
                case "Back to Main Menu":
                    dispose(); // Close the AdminFrame
                    new MainMenu().setVisible(true);
                    break;
                default:
                    showStyledMessageDialog("Invalid option selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Styled input dialog
    private String showStyledInputDialog(String message, String title) {
        JTextField textField = new JTextField();
        textField.setFont(gameFont);
        textField.setForeground(TEXT_COLOR);
        textField.setBackground(new Color(60, 63, 65));
        textField.setCaretColor(TEXT_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(ACCENT_COLOR, 1, true),
            BorderFactory.createEmptyBorder(5, 7, 5, 7)
        ));

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(gameFont);
        messageLabel.setForeground(TEXT_COLOR);
        
        panel.add(messageLabel, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(this, panel, title, 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            return textField.getText();
        }
        return null;
    }

    // Styled message dialog
    private void showStyledMessageDialog(String message, String title, int messageType) {
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(gameFont);
        messageLabel.setForeground(TEXT_COLOR);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.add(messageLabel, BorderLayout.CENTER);
        
        JOptionPane.showMessageDialog(this, panel, title, messageType);
    }

    public void appendToDisplay(String message) {
        displayArea.append(message + "\n");
        // Auto-scroll to bottom
        displayArea.setCaretPosition(displayArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        AdminLogic adminLogic = new AdminLogic();
        SwingUtilities.invokeLater(() -> new AdminFrame(adminLogic).setVisible(true));
    }
}