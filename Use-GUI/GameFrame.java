import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

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
    
    public GameFrame() {
        setTitle("Word Trivia Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Status bar (top)
        statusLabel = new JLabel("Welcome to Word Trivia!", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(statusLabel, BorderLayout.NORTH);

        // Board panel (center)
        boardPanel = new JPanel();
        boardPanel.setBackground(Color.WHITE);
        add(boardPanel, BorderLayout.CENTER);

        // Bottom panel (clue, answer input)
        JPanel bottomPanel = new JPanel(new BorderLayout());

        clueArea = new JTextArea(3, 30);
        clueArea.setEditable(false);
        clueArea.setLineWrap(true);
        clueArea.setWrapStyleWord(true);
        clueArea.setFont(new Font("Serif", Font.ITALIC, 14));
        bottomPanel.add(new JScrollPane(clueArea), BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Your Answer:"));
        answerField = new JTextField(20);
        inputPanel.add(answerField);
        submitButton = new JButton("Submit");
        inputPanel.add(submitButton);

        playAgainButton = new JButton("Play Again");
        playAgainButton.setVisible(false);
        inputPanel.add(playAgainButton);

        JButton backButton = new JButton("Back");
        inputPanel.add(backButton);

        bottomPanel.add(inputPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add listeners
        submitButton.addActionListener(e -> checkAnswer());
        playAgainButton.addActionListener(e -> initGame());
        backButton.addActionListener(e -> goBackToMainMenu());

        setLocationRelativeTo(null); 

        // Launch setup
        initGame();
    }

    private void initGame() {
        // Reset everything
        words.clear();
        clues.clear();
        currentRow = 0;
        clueArea.setText("");
        answerField.setText("");
        submitButton.setEnabled(true);
        playAgainButton.setVisible(false);
    
        // Ask board size
        try {
            rows = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter number of rows:"));
            cols = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter number of columns:"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Defaulting to 3x3.");
            rows = 3;
            cols = 3;
        }
    
        // Prompt for filename
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
    
        // Read and process file
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
            // Final entry
            if (current != null && !clueList.isEmpty()) {
                wordCluePairs.add(new WordWithClues(current, new ArrayList<>(clueList)));
            }
    
            // Shuffle and split
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
    
            // Build letter board with correct mapping
            List<LetterWithClues> allLetters = new ArrayList<>();
            for (int i = 0; i < words.size(); i++) {
                String word = words.get(i);
                List<String[]> wordClues = clues.get(i);
                for (char c : word.toCharArray()) {
                    allLetters.add(new LetterWithClues(c, i, wordClues));
                }
            }
    
            // Shuffle letters
            Collections.shuffle(allLetters);
    
            // Place letters on board
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
    
            // Draw board
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
        List<String[]> clues;  // Store clues as List<String[]>
    
        LetterWithClues(char letter, int wordIndex, List<String[]> clues) {
            this.letter = letter;
            this.wordIndex = wordIndex;
            this.clues = clues;
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
    
                JButton button = new JButton(String.valueOf(boardLetters[r][c]));
                button.setFont(new Font("Arial", Font.BOLD, 20));
                button.setBackground(Color.LIGHT_GRAY);
    
                int row = r;
                int col = c;
    
                button.addActionListener(e -> handleButtonClick(row, col, button));
                boardButtons[r][c] = button;
                boardPanel.add(button); // Assuming you have a JPanel named boardPanel
            }
        }
    }

    private void handleButtonClick(int row, int col, JButton button) {
        // Prevent selecting another cell while awaiting an answer
        if (awaitingAnswer) {
            JOptionPane.showMessageDialog(this, "Please answer the current question before selecting another.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        if (cellStatus[row][col] != CellStatus.UNANSWERED) {
            JOptionPane.showMessageDialog(this, "This letter has already been answered.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        awaitingAnswer = true;
    
        if (awaitingAnswer) {
            JOptionPane.showMessageDialog(this, "Please answer the current question before selecting another.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Disable all other buttons except the current one
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                boardButtons[r][c].setEnabled(r == row && c == col);
            }
        }
    
        // Input panel for answer
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Your Answer:"));
    
        JTextField userInput = new JTextField(10);
        inputPanel.add(userInput);
    
        int result = JOptionPane.showConfirmDialog(
            this,
            inputPanel,
            "Enter your answer",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
    
        if (result == JOptionPane.OK_OPTION) {
            String userAnswer = userInput.getText().trim();
            String correctWord = boardWords[row][col];
    
            if (userAnswer.isEmpty() || !userAnswer.equalsIgnoreCase(correctWord)) {
                // Incorrect
                cellStatus[row][col] = CellStatus.WRONG;
                button.setBackground(Color.RED);
                button.setText(button.getText() + "*");
                JOptionPane.showMessageDialog(this, "Incorrect! The correct word was: " + correctWord, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Correct
                cellStatus[row][col] = CellStatus.CORRECT;
                button.setBackground(Color.GREEN);
                JOptionPane.showMessageDialog(this, "Correct!", "Success", JOptionPane.INFORMATION_MESSAGE);
    
                // Check if row is complete
                boolean rowComplete = true;
                for (int c = 0; c < cols; c++) {
                    if (cellStatus[row][c] == CellStatus.UNANSWERED) {
                        rowComplete = false;
                        break;
                    }
                }
    
                if (rowComplete) {
                    currentRow++;
                    if (currentRow >= rows) {
                        statusLabel.setText("🎉 You finished the game!");
                        submitButton.setEnabled(false);
                        playAgainButton.setVisible(true);
                    } else {
                        statusLabel.setText("✅ Row complete! Move to the next row.");
                    }
                }
            }
        }
        
        // Re-enable all unanswered buttons in the current row
        for (int c = 0; c < cols; c++) {
            if (cellStatus[row][c] == CellStatus.UNANSWERED) {
                boardButtons[row][c].setEnabled(true);
            } else {
                boardButtons[row][c].setEnabled(false);
            }
        }
    
        awaitingAnswer = false;
    }
    
    private void drawBoard() {
        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(rows, cols, 10, 10));
    
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JButton btn;
                if (cellStatus[r][c] == CellStatus.CORRECT) {
                    btn = new JButton("✅");
                }else if (cellStatus[r][c] == CellStatus.WRONG) {
                    btn = new JButton("❌");
                } else {
                    btn = new JButton(String.valueOf(boardLetters[r][c]));
                }
    
                btn.setFont(new Font("Arial", Font.BOLD, 20));
                btn.setOpaque(true);
    
                // Set background color based on status
                switch (cellStatus[r][c]) {
                    case CORRECT:
                        btn.setBackground(new Color(144, 238, 144)); // light green
                        break;
                    case WRONG:
                        btn.setBackground(new Color(255, 99, 71)); // tomato red
                        break;
                    case UNANSWERED:
                    default:
                        btn.setBackground(Color.CYAN);
                        break;
                }
    
                btn.setEnabled(r == currentRow && cellStatus[r][c] == CellStatus.UNANSWERED);
    
                int finalR = r, finalC = c;
                btn.addActionListener(e -> {
                    currentWord = boardWords[finalR][finalC];
                    currentR = finalR;
                    currentC = finalC;
    
                    int wordIndex = words.indexOf(currentWord);
                    List<String[]> wordClues = clues.get(wordIndex);
                    String[] clue = wordClues.get(new Random().nextInt(wordClues.size()));
                    clueArea.setText("Clue: " + clue[0] + " - " + clue[1]);
                    clueArea.setFont(new Font("Times New Roman", Font.ITALIC, 18));
                    statusLabel.setText("Enter your guess for '" + boardLetters[finalR][finalC] + "'");
                    answerField.requestFocus();
                });
    
                boardButtons[r][c] = btn;
                boardPanel.add(btn);
            }
        }
    
        boardPanel.revalidate();
        boardPanel.repaint();
    }         

    private void checkAnswer() {
        String answer = answerField.getText().trim();
    
        if (answer.equalsIgnoreCase(currentWord)) {
            cellStatus[currentR][currentC] = CellStatus.CORRECT;
            boardButtons[currentR][currentC].setBackground(Color.GREEN);
            boardButtons[currentR][currentC].setText("✅");
            
            // Check if all buttons in current row are answered
            currentRow++;
            if (currentRow >= rows) {
                statusLabel.setText("🎉 You win! Great job!");
                submitButton.setEnabled(false);
                playAgainButton.setVisible(true);
            } else {
                statusLabel.setText("✅ Correct! Proceed to row " + (currentRow + 1) + ".");
            }
        } else {
            statusLabel.setText("❌ Wrong answer. Try another letter.");
            boardButtons[currentR][currentC].setEnabled(false); // prevent further clicks
            cellStatus[currentR][currentC] = CellStatus.WRONG;
            boardButtons[currentR][currentC].setBackground(Color.RED);
            boardButtons[currentR][currentC].setText("❌");
            JOptionPane.showMessageDialog(this, "Incorrect! The correct word was: " + currentWord, "Error", JOptionPane.ERROR_MESSAGE);
        
    
            // Check if all letters in current row are answered
            boolean anyLeft = false;
            for (int c = 0; c < cols; c++) {
                if (cellStatus[currentRow][c] == CellStatus.UNANSWERED) {
                    anyLeft = true;
                    break;
                }
            }
            
            if (!anyLeft) {
                statusLabel.setText("💀 No more guesses in this row. You lose.");
                submitButton.setEnabled(false);
                playAgainButton.setVisible(true);
            }
        }
    
        answerField.setText("");
        drawBoard(); // Redraw the board to disable or refresh tiles
    }

    private void goBackToMainMenu() {
        dispose(); // Close the current GameFrame
        new MainMenu().setVisible(true); // Open the main menu
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameFrame gf = new GameFrame();
            gf.setVisible(true);
        });
    }
}
