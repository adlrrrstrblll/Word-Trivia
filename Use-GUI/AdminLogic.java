import java.io.*;
import java.util.*;
import javax.swing.*;

public class AdminLogic {
    private final int MAX_WORDS = 150;
    private final int MAX_TRIVIA = 10;

    private String[] words = new String[MAX_WORDS];
    private String[][] triviaRel = new String[MAX_WORDS][MAX_TRIVIA];
    private String[][] triviaVal = new String[MAX_WORDS][MAX_TRIVIA];
    private int[] triviaCount = new int[MAX_WORDS];

    private int wordCount = 0;

    // Add a new word
    public void addWordFromGUI(String word, JFrame frame) {
        if (wordCount >= MAX_WORDS) {
            JOptionPane.showMessageDialog(frame, "Word limit reached.");
            return;
        }

        if (word.isEmpty() || findWord(word) != -1) {
            JOptionPane.showMessageDialog(frame, "Word already exists or is invalid.");
            return;
        }

        words[wordCount] = word;
        wordCount++;
        JOptionPane.showMessageDialog(frame, "Word added successfully.");
    }

    // Add trivia to an existing word
    public void addTriviaFromGUI(String word, String trivia, JFrame frame) {
        int index = findWord(word);
        if (index == -1) {
            JOptionPane.showMessageDialog(frame, "Word not found.");
            return;
        }
    
        if (triviaCount[index] >= MAX_TRIVIA) {
            JOptionPane.showMessageDialog(frame, "Trivia limit reached for this word.");
            return;
        }
    
        String[] parts = trivia.split(":", 2);
        if (parts.length != 2) {
            JOptionPane.showMessageDialog(frame, "Invalid trivia format. Use 'relation:value'.");
            return;
        }
    
        triviaRel[index][triviaCount[index]] = parts[0].trim();
        triviaVal[index][triviaCount[index]] = parts[1].trim();
        triviaCount[index]++;
        JOptionPane.showMessageDialog(frame, "Trivia added successfully.");
    }

    // Modify an existing word
    public void modifyWordFromGUI(String oldWord, String newWord, JFrame frame) {
        int index = findWord(oldWord);
        if (index == -1) {
            JOptionPane.showMessageDialog(frame, "Word not found.");
            return;
        }

        if (findWord(newWord) != -1) {
            JOptionPane.showMessageDialog(frame, "New word already exists.");
            return;
        }

        words[index] = newWord;
        JOptionPane.showMessageDialog(frame, "Word modified successfully.");
    }

    // Delete a word
    public void deleteWordFromGUI(String word, JFrame frame) {
        int index = findWord(word);
        if (index == -1) {
            JOptionPane.showMessageDialog(frame, "Word not found.");
            return;
        }

        for (int i = index; i < wordCount - 1; i++) {
            words[i] = words[i + 1];
            triviaCount[i] = triviaCount[i + 1];
            triviaRel[i] = Arrays.copyOf(triviaRel[i + 1], MAX_TRIVIA);
            triviaVal[i] = Arrays.copyOf(triviaVal[i + 1], MAX_TRIVIA);
        }

        words[wordCount - 1] = null;
        triviaRel[wordCount - 1] = new String[MAX_TRIVIA];
        triviaVal[wordCount - 1] = new String[MAX_TRIVIA];
        wordCount--;

        JOptionPane.showMessageDialog(frame, "Word deleted successfully.");
    }

    // Delete trivia from a word
    public void deleteTriviaFromGUI(String word, String trivia, JFrame frame) {
        int index = findWord(word);
        if (index == -1) {
            JOptionPane.showMessageDialog(frame, "Word not found.");
            return;
        }

        for (int i = 0; i < triviaCount[index]; i++) {
            if ((triviaRel[index][i] + ": " + triviaVal[index][i]).equalsIgnoreCase(trivia)) {
                for (int j = i; j < triviaCount[index] - 1; j++) {
                    triviaRel[index][j] = triviaRel[index][j + 1];
                    triviaVal[index][j] = triviaVal[index][j + 1];
                }
                triviaRel[index][triviaCount[index] - 1] = null;
                triviaVal[index][triviaCount[index] - 1] = null;
                triviaCount[index]--;
                JOptionPane.showMessageDialog(frame, "Trivia deleted successfully.");
                return;
            }
        }

        JOptionPane.showMessageDialog(frame, "Trivia not found.");
    }

    // View all words
    public String viewWords() {
        if (wordCount == 0) {
            return "No words to view.";
        }

        StringBuilder sb = new StringBuilder("=== List of Words ===\n");
        for (int i = 0; i < wordCount; i++) {
            sb.append((i + 1)).append("] ").append(words[i]).append("\n");
        }
        return sb.toString();
    }

    // View all trivia
    public String viewTrivia() {
        if (wordCount == 0) {
            return "No trivia to view.";
        }
    
        StringBuilder sb = new StringBuilder("=== List of Trivia ===\n");
        for (int i = 0; i < wordCount; i++) {
            sb.append("Word: ").append(words[i]).append("\n");
            for (int j = 0; j < triviaCount[i]; j++) {
                if (triviaRel[i][j] != null && triviaVal[i][j] != null) {
                    sb.append("  - ").append(triviaRel[i][j]).append(": ").append(triviaVal[i][j]).append("\n");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public void modifyTriviaFromGUI(String word, int triviaIndex, String newTrivia, JFrame frame) {
        int index = findWord(word);
        if (index == -1) {
            JOptionPane.showMessageDialog(frame, "Word not found.");
            return;
        }
    
        if (triviaIndex < 0 || triviaIndex >= triviaCount[index]) {
            JOptionPane.showMessageDialog(frame, "Invalid trivia index.");
            return;
        }
    
        String[] parts = newTrivia.split(":", 2);
        if (parts.length != 2) {
            JOptionPane.showMessageDialog(frame, "Invalid trivia format. Use 'relation:value'.");
            return;
        }
    
        triviaRel[index][triviaIndex] = parts[0].trim();
        triviaVal[index][triviaIndex] = parts[1].trim();
        JOptionPane.showMessageDialog(frame, "Trivia modified successfully.");
    }

    // Export data to a file
    public void exportData(String filename, JFrame frame) {
        if (wordCount == 0) {
            JOptionPane.showMessageDialog(frame, "No data to export.");
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (int i = 0; i < wordCount; i++) {
                writer.println("Object: " + words[i]);
                for (int j = 0; j < triviaCount[i]; j++) {
                    writer.println(triviaRel[i][j] + ": " + triviaVal[i][j]);
                }
                writer.println();
            }
            JOptionPane.showMessageDialog(frame, "Data exported successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error exporting data: " + e.getMessage());
        }
    }

    // Import data from a file
    public void importData(String filename, JFrame frame) {
        File file = new File(filename);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(frame, "File not found.");
            return;
        }
    
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String currentWord = null;
            int clueCount = 0;
    
            while ((line = reader.readLine()) != null) {
                line = line.trim();
    
                if (line.isEmpty()) {
                    if (currentWord != null) {
                        int index = findWord(currentWord);
                        if (index == -1) {
                            index = wordCount++;
                            words[index] = currentWord;
                        }
                        triviaCount[index] = clueCount;
                    }
                    currentWord = null;
                    clueCount = 0;
                } else if (line.startsWith("Object: ")) {
                    currentWord = line.substring(8).trim();
                } else if (currentWord != null && line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length == 2 && clueCount < MAX_TRIVIA) {
                        int index = findWord(currentWord);
                        if (index == -1) {
                            index = wordCount++;
                            words[index] = currentWord;
                        }
                        triviaRel[index][clueCount] = parts[0].trim();
                        triviaVal[index][clueCount] = parts[1].trim();
                        clueCount++;
                    }
                }
            }
    
            JOptionPane.showMessageDialog(frame, "Data imported successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error importing data: " + e.getMessage());
        }
    }

    // Helper function to find a word
    public int findWord(String word) {
        for (int i = 0; i < words.length; i++) {
            if (words[i] != null && words[i].equalsIgnoreCase(word)) {
                return i;
            }
        }
        return -1; // Word not found
    }

    public int getTriviaCount(int index) {
        return triviaCount[index];
    }
    
    public String getTriviaRel(int wordIndex, int triviaIndex) {
        return triviaRel[wordIndex][triviaIndex];
    }
    
    public String getTriviaVal(int wordIndex, int triviaIndex) {
        return triviaVal[wordIndex][triviaIndex];
    }
}