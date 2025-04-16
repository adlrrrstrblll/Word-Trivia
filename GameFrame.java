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
    private boolean[][] answered;

    private int rows;
    private int cols;
    private int currentRow = 0;
    private String currentWord;
    private int currentR, currentC;

    public GameFrame() {
        setTitle("Word Trivia Game");
        setSize(700, 600);
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

        bottomPanel.add(inputPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add listeners
        submitButton.addActionListener(e -> checkAnswer());
        playAgainButton.addActionListener(e -> initGame());

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

        // Load file
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Trivia File");
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line, current = null;
            List<String[]> clueList = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    if (current != null && !clueList.isEmpty()) {
                        words.add(current);
                        clues.add(clueList);
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
                words.add(current);
                clues.add(clueList);
            }

            if (words.size() < rows * cols) {
                JOptionPane.showMessageDialog(this, "Not enough valid words for this board size.");
                return;
            }

            setupBoard();
            drawBoard();
            statusLabel.setText("Game loaded! Choose a letter from row 1.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
        }
    }

    private void setupBoard() {
        boardLetters = new char[rows][cols];
        boardWords = new String[rows][cols];
        boardButtons = new JButton[rows][cols];
        answered = new boolean[rows][cols];

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < words.size(); i++) indices.add(i);
        Collections.shuffle(indices);

        int wordIdx = 0;
        for (int r = 0; r < rows; r++) {
            Set<Character> used = new HashSet<>();
            int c = 0;
            while (c < cols && wordIdx < indices.size()) {
                int idx = indices.get(wordIdx++);
                String word = words.get(idx);
                char first = Character.toUpperCase(word.charAt(0));
                if (!used.contains(first)) {
                    boardWords[r][c] = word;
                    boardLetters[r][c] = first;
                    used.add(first);
                    c++;
                }
            }
        }
    }

    private void drawBoard() {
        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(rows, cols, 10, 10));
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JButton btn = new JButton(answered[r][c] ? "*" : String.valueOf(boardLetters[r][c]));
                btn.setFont(new Font("Arial", Font.BOLD, 20));
                btn.setBackground(Color.CYAN);
                btn.setOpaque(true);
                btn.setEnabled(r == currentRow && !answered[r][c]);

                int finalR = r, finalC = c;
                btn.addActionListener(e -> {
                    currentWord = boardWords[finalR][finalC];
                    currentR = finalR;
                    currentC = finalC;

                    int wordIndex = words.indexOf(currentWord);
                    List<String[]> wordClues = clues.get(wordIndex);
                    String[] clue = wordClues.get(new Random().nextInt(wordClues.size()));
                    clueArea.setText("Clue: " + clue[0] + " — " + clue[1]);
                    statusLabel.setText("Enter your guess for '" + boardLetters[finalR][finalC] + "'");
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
            statusLabel.setText("Correct! Proceeding to next row...");
            boardButtons[currentR][currentC].setBackground(new Color(144, 238, 144)); // light green
            answered[currentR][currentC] = true;
            currentRow++;

            if (currentRow >= rows) {
                statusLabel.setText("🎉 You win! Great job!");
                submitButton.setEnabled(false);
                playAgainButton.setVisible(true);
            }
        } else {
            statusLabel.setText("Wrong! Try another letter.");
            boardButtons[currentR][currentC].setBackground(new Color(255, 99, 71)); // tomato red
            answered[currentR][currentC] = true;

            // Check if all letters in current row are wrong
            boolean anyLeft = false;
            for (int c = 0; c < cols; c++) {
                if (!answered[currentRow][c]) {
                    anyLeft = true;
                    break;
                }
            }
            if (!anyLeft) {
                statusLabel.setText("No more guesses in this row. You lose. 😞");
                submitButton.setEnabled(false);
                playAgainButton.setVisible(true);
            }
        }

        answerField.setText("");
        drawBoard();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameFrame gf = new GameFrame();
            gf.setVisible(true);
        });
    }
}
