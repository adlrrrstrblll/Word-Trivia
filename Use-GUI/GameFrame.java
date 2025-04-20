import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;

public class GameFrame extends JFrame {
    private JPanel boardPanel;
    private JTextArea clueArea;
    private JTextField answerField;
    private JButton submitButton, playAgainButton;
    private JLabel statusLabel;

    private List<String> words = new ArrayList<>();
    private List<List<String[]>> clues = new ArrayList<>();
    private String[][] boardWords;
    private char[][] boardLetters;
    private JButton[][] boardButtons;
    CellStatus[][] cellStatus;

    private int rows;
    private int cols;
    private int currentRow = 0;
    private String currentWord;
    private int currentR, currentC;
    private boolean awaitingAnswer = false;

    enum CellStatus {
        UNANSWERED,
        CORRECT,
        WRONG
    }

    // Custom colors
    private final Color BACKGROUND_COLOR = new Color(32, 33, 36);  // Dark background
    private final Color ACCENT_COLOR = new Color(138, 180, 248);   // Blue accent
    private final Color TEXT_COLOR = new Color(248, 249, 250);     // Light text
    private final Color CORRECT_COLOR = new Color(52, 199, 89);    // Green for correct
    private final Color WRONG_COLOR = new Color(255, 69, 58);      // Red for wrong
    private final Color UNANSWERED_COLOR = new Color(72, 118, 255);// Blue for unanswered

    // Background image
    private Image backgroundImage;
    private boolean hasBackgroundImage = false;

    // Custom font
    private Font gameFont;
    private Font titleFont;
    private Font buttonFont;

    public GameFrame() {
        // Load custom resources
        loadResources();

        setTitle("Word Trivia Game");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use custom panel with background image
        JPanel mainPanel = new BackgroundPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Status bar (top)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        statusLabel = new JLabel("Welcome to Word Trivia!", SwingConstants.CENTER);
        statusLabel.setFont(titleFont);
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        headerPanel.add(statusLabel, BorderLayout.CENTER);

        // Add logo (optional)
        try {
            ImageIcon logoIcon = new ImageIcon(new URL("https://i.imgur.com/8NXpgQv.png"));
            Image scaledImage = logoIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
            headerPanel.add(logoLabel, BorderLayout.WEST);
        } catch (Exception e) {
            System.out.println("Logo image could not be loaded");
        }

        add(headerPanel, BorderLayout.NORTH);

        // Board panel (center) with stylish border
        boardPanel = new JPanel();
        boardPanel.setOpaque(false);
        Border boardBorder = new CompoundBorder(
            new LineBorder(ACCENT_COLOR, 2, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );
        boardPanel.setBorder(boardBorder);
        add(boardPanel, BorderLayout.CENTER);

        // Bottom panel (clue, answer input)
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);

        // Stylish clue area
        JPanel cluePanel = new JPanel(new BorderLayout());
        cluePanel.setOpaque(false);
        cluePanel.setBorder(new TitledBorder(
            new LineBorder(ACCENT_COLOR, 2, true),
            "CLUE",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            gameFont,
            TEXT_COLOR
        ));

        clueArea = new JTextArea(3, 30);
        clueArea.setEditable(false);
        clueArea.setLineWrap(true);
        clueArea.setWrapStyleWord(true);
        clueArea.setFont(gameFont);
        clueArea.setForeground(TEXT_COLOR);
        clueArea.setOpaque(false);
        clueArea.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JScrollPane clueScroll = new JScrollPane(clueArea);
        clueScroll.setOpaque(false);
        clueScroll.getViewport().setOpaque(false);
        clueScroll.setBorder(null);

        cluePanel.add(clueScroll, BorderLayout.CENTER);
        bottomPanel.add(cluePanel, BorderLayout.NORTH);

        // Input panel with styled components
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        inputPanel.setOpaque(false);

        JLabel answerLabel = new JLabel("Your Answer:");
        answerLabel.setFont(gameFont);
        answerLabel.setForeground(TEXT_COLOR);
        inputPanel.add(answerLabel);

        answerField = new JTextField(20);
        answerField.setFont(gameFont);
        answerField.setCaretColor(TEXT_COLOR);
        answerField.setForeground(TEXT_COLOR);
        answerField.setBackground(new Color(60, 63, 65));
        answerField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(ACCENT_COLOR, 1, true),
            BorderFactory.createEmptyBorder(5, 7, 5, 7)
        ));
        inputPanel.add(answerField);

        submitButton = createStyledButton("Submit");
        playAgainButton = createStyledButton("Play Again");
        playAgainButton.setVisible(false);
        JButton backButton = createStyledButton("Back");

        inputPanel.add(submitButton);
        inputPanel.add(playAgainButton);
        inputPanel.add(backButton);

        bottomPanel.add(inputPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add listeners
        submitButton.addActionListener(e -> checkAnswer());
        playAgainButton.addActionListener(e -> initGame());
        backButton.addActionListener(e -> goBackToMainMenu());

        // Add key listener to answer field for enter key
        answerField.addActionListener(e -> submitButton.doClick());

        setLocationRelativeTo(null);

        // Launch setup
        initGame();
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
        button.setPreferredSize(new Dimension(120, 40));

        return button;
    }

    private void loadResources() {
        // Load custom font
        try {
            // Try to load Roboto font from Google
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, 
                new URL("https://github.com/googlefonts/roboto/raw/main/src/hinted/Roboto-Regular.ttf").openStream());
            gameFont = baseFont.deriveFont(Font.PLAIN, 16f);
            titleFont = baseFont.deriveFont(Font.BOLD, 24f);
            buttonFont = baseFont.deriveFont(Font.BOLD, 16f);

            // Register font
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(baseFont);
        } catch (Exception e) {
            // Fallback fonts if custom font fails to load
            gameFont = new Font("SansSerif", Font.PLAIN, 16);
            titleFont = new Font("SansSerif", Font.BOLD, 24);
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

    private void setupBoard() {
        boardLetters = new char[rows][cols];
        boardWords = new String[rows][cols];
        boardButtons = new JButton[rows][cols];
        cellStatus = new CellStatus[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                boardLetters[r][c] = words.get(r * cols + c).charAt(0); // First letter of the word
                boardWords[r][c] = words.get(r * cols + c);
                cellStatus[r][c] = CellStatus.UNANSWERED;

                // Create fancy game board buttons
                JButton button = new JButton(String.valueOf(boardLetters[r][c])) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        if (isEnabled()) {
                            g2.setColor(UNANSWERED_COLOR);
                        } else {
                            g2.setColor(new Color(50, 50, 50));
                        }

                        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

                        if (isEnabled() && getModel().isRollover()) {
                            g2.setColor(new Color(255, 255, 255, 80));
                            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                        }

                        g2.setColor(TEXT_COLOR);
                        g2.setFont(new Font("Arial", Font.BOLD, 28));

                        FontMetrics fm = g2.getFontMetrics();
                        int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                        int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

                        g2.drawString(getText(), textX, textY);
                        g2.dispose();
                    }
                };

                button.setOpaque(false);
                button.setContentAreaFilled(false);
                button.setBorderPainted(false);
                button.setFocusPainted(false);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.setPreferredSize(new Dimension(80, 80));

                int row = r;
                int col = c;

                button.addActionListener(e -> handleButtonClick(row, col, button));
                boardButtons[r][c] = button;
            }
        }
    }

    private void drawBoard() {
        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(rows, cols, 15, 15));

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                final int finalR = r;
                final int finalC = c;
                
                final JButton btn = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        // Determine background color based on status
                        Color bgColor;
                        switch (cellStatus[finalR][finalC]) {
                            case CORRECT:
                                bgColor = CORRECT_COLOR;
                                break;
                            case WRONG:
                                bgColor = WRONG_COLOR;
                                break;
                            case UNANSWERED:
                            default:
                                bgColor = isEnabled() ? UNANSWERED_COLOR : new Color(60, 63, 65);
                                break;
                        }

                        // Draw rounded button background
                        g2.setColor(bgColor);
                        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));

                        // Add hover effect
                        if (isEnabled() && getModel().isRollover()) {
                            g2.setColor(new Color(255, 255, 255, 60));
                            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
                        }

                        // Draw text/icon
                        String buttonText;
                        if (cellStatus[finalR][finalC] == CellStatus.CORRECT) {
                            buttonText = "✓";
                        } else if (cellStatus[finalR][finalC] == CellStatus.WRONG) {
                            buttonText = "✗";
                        } else {
                            buttonText = String.valueOf(boardLetters[finalR][finalC]);
                        }

                        // Set font based on text content
                        Font textFont;
                        if (buttonText.equals("✓") || buttonText.equals("✗")) {
                            textFont = new Font("Dialog", Font.BOLD, 32);
                        } else {
                            textFont = new Font("Dialog", Font.BOLD, 28);
                        }

                        g2.setFont(textFont);
                        g2.setColor(TEXT_COLOR);

                        FontMetrics fm = g2.getFontMetrics();
                        int textX = (getWidth() - fm.stringWidth(buttonText)) / 2;
                        int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

                        g2.drawString(buttonText, textX, textY);

                        // Draw border
                        g2.setStroke(new BasicStroke(2));
                        g2.setColor(new Color(0, 0, 0, 80));
                        g2.draw(new RoundRectangle2D.Double(1, 1, getWidth()-2, getHeight()-2, 15, 15));

                        g2.dispose();
                    }
                };

                btn.setOpaque(false);
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

                // Set button state
                btn.setEnabled(r == currentRow && cellStatus[r][c] == CellStatus.UNANSWERED);

                btn.addActionListener(e -> handleButtonClick(finalR, finalC, btn));

                boardButtons[r][c] = btn;
                boardPanel.add(btn);
            }
        }

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    // ...existing code...
    private void handleButtonClick(int row, int col, JButton button) {
        if (awaitingAnswer) {
            // If we're clicking on the same button that's already active, cycle through clues
            if (row == currentR && col == currentC) {
                int wordIndex = words.indexOf(currentWord);
                List<String[]> wordClues = clues.get(wordIndex);
                
                // Generate a different clue than the current one
                if (wordClues.size() > 1) {
                    String currentClueText = clueArea.getText().replace("Clue: ", "");
                    String[] newClue;
                    do {
                        newClue = wordClues.get(new Random().nextInt(wordClues.size()));
                    } while ((newClue[0] + " - " + newClue[1]).equals(currentClueText) && wordClues.size() > 1);
                    
                    clueArea.setText("Clue: " + newClue[0] + " - " + newClue[1]);
                    statusLabel.setText("New clue for '" + boardLetters[row][col] + "' - still awaiting your answer");
                } else {
                    statusLabel.setText("No more clues available for this word");
                }
                return;
            }
            
            JOptionPane.showMessageDialog(this, "Please answer the current question before selecting another.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cellStatus[row][col] != CellStatus.UNANSWERED) {
            JOptionPane.showMessageDialog(this, "This letter has already been answered.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (row != currentRow) {
            JOptionPane.showMessageDialog(this, "Please select letters from row " + (currentRow + 1) + ".", 
                "Wrong Row", JOptionPane.WARNING_MESSAGE);
            return;
        }

        awaitingAnswer = true;
        currentWord = boardWords[row][col];
        currentR = row;
        currentC = col;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                boardButtons[r][c].setEnabled(false);
            }
        }
        
        // Enable the current button to allow clicking for more clues
        boardButtons[row][col].setEnabled(true);

        int wordIndex = words.indexOf(currentWord);
        List<String[]> wordClues = clues.get(wordIndex);
        String[] clue = wordClues.get(new Random().nextInt(wordClues.size()));
        clueArea.setText("Clue: " + clue[0] + " - " + clue[1]);
        clueArea.setFont(new Font("Times New Roman", Font.ITALIC, 18));
        statusLabel.setText("Enter your guess for '" + boardLetters[row][col] + "' (click again for more clues)");

        answerField.setText("");
        answerField.requestFocus();
    }

    private void checkAnswer() {
        String answer = answerField.getText().trim();

        if (answer.equalsIgnoreCase(currentWord)) {
            cellStatus[currentR][currentC] = CellStatus.CORRECT;

            currentRow++;
            if (currentRow >= rows) {
                statusLabel.setText("🎉 You win! Great job!");
                submitButton.setEnabled(false);
                playAgainButton.setVisible(true);
            } else {
                statusLabel.setText("✅ Correct! Proceed to row " + (currentRow + 1) + ".");
            }
        } else {
            cellStatus[currentR][currentC] = CellStatus.WRONG;
            statusLabel.setText("❌ Wrong answer. Choose another letter in this row.");
            JOptionPane.showMessageDialog(this, "Incorrect! The correct word was: " + currentWord, "Error", JOptionPane.ERROR_MESSAGE);

            boolean rowHasUnanswered = false;
            for (int c = 0; c < cols; c++) {
                if (cellStatus[currentRow][c] == CellStatus.UNANSWERED) {
                    rowHasUnanswered = true;
                    break;
                }
            }

            if (!rowHasUnanswered) {
                statusLabel.setText("💀 No more guesses in this row. Game over.");
                submitButton.setEnabled(false);
                playAgainButton.setVisible(true);
            }
        }

        awaitingAnswer = false;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                boardButtons[r][c].setEnabled(r == currentRow && cellStatus[r][c] == CellStatus.UNANSWERED);
            }
        }

        answerField.setText("");
        drawBoard();
    }

    private void goBackToMainMenu() {
        dispose();
        new MainMenu().setVisible(true);
    }

    private void initGame() {
        words.clear();
        clues.clear();
        currentRow = 0;
        clueArea.setText("");
        answerField.setText("");
        submitButton.setEnabled(true);
        playAgainButton.setVisible(false);

        try {
            rows = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter number of rows:"));
            cols = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter number of columns:"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Defaulting to 3x3.");
            rows = 3;
            cols = 3;
        }

        String filename = JOptionPane.showInputDialog(this, "Enter trivia filename (e.g. game.txt):");
        if (filename == null || filename.isBlank()) {
            JOptionPane.showMessageDialog(this, "No filename entered. Aborting.");
            return;
        }
        File file = new File(filename);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "File not found: " + filename);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line, current = null;
            List<String[]> clueList = new ArrayList<>();
            List<WordWithClues> wordCluePairs = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    if (current != null && !clueList.isEmpty()) {
                        wordCluePairs.add(new WordWithClues(current, new ArrayList<>(clueList)));
                    }
                    current = null;
                    clueList = new ArrayList<>();
                } else if (line.startsWith("Object: ")) {
                    current = line.substring(8).trim();
                } else if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    clueList.add(new String[]{parts[0].trim(), parts[1].trim()});
                }
            }
            if (current != null && !clueList.isEmpty()) {
                wordCluePairs.add(new WordWithClues(current, new ArrayList<>(clueList)));
            }

            Collections.shuffle(wordCluePairs);
            for (int i = 0; i < rows * cols && i < wordCluePairs.size(); i++) {
                words.add(wordCluePairs.get(i).word);
                clues.add(wordCluePairs.get(i).clues);
            }

            if (words.size() < rows * cols) {
                JOptionPane.showMessageDialog(this,
                    "Not enough valid words for this board size. " +
                    "You need at least " + (rows * cols) + " words, but only " + words.size() + " are available.",
                    "Insufficient Words",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<LetterWithClues> allLetters = new ArrayList<>();
            for (int i = 0; i < words.size(); i++) {
                String word = words.get(i);
                List<String[]> wordClues = clues.get(i);
                for (char c : word.toCharArray()) {
                    allLetters.add(new LetterWithClues(c, i, wordClues));
                }
            }

            Collections.shuffle(allLetters);

            String[][] gameBoard = new String[rows][cols];
            int letterIndex = 0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (letterIndex < allLetters.size()) {
                        gameBoard[i][j] = String.valueOf(allLetters.get(letterIndex).letter);
                        letterIndex++;
                    }
                }
            }

            setupBoard();
            drawBoard();
            statusLabel.setText("Game loaded! Choose a letter from row 1.");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error reading file: " + e.getMessage());
        }
    }

    private static class WordWithClues {
        String word;
        List<String[]> clues;

        WordWithClues(String word, List<String[]> clues) {
            this.word = word;
            this.clues = clues;
        }
    }

    private static class LetterWithClues {
        char letter;
        int wordIndex;
        List<String[]> clues;

        LetterWithClues(char letter, int wordIndex, List<String[]> clues) {
            this.letter = letter;
            this.wordIndex = wordIndex;
            this.clues = clues;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameFrame gf = new GameFrame();
            gf.setVisible(true);
        });
    }
}
