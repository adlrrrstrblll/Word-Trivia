import java.io.*;
import java.util.*;

public class Game {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        gamePhase();
    }

    public static void gamePhase() {
        System.out.print("Enter filename to load words from: ");
        String filename = sc.nextLine().trim();

        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("File not found. Please check the filename and try again.");
            return;
        }

        ArrayList<String> loadedWords = new ArrayList<>();
        ArrayList<ArrayList<String[]>> loadedClues = new ArrayList<>();

        // Load words and clues from file
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String currentWord = null;
            ArrayList<String[]> currentClues = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    if (currentWord != null) {
                        loadedWords.add(currentWord);
                        loadedClues.add(currentClues);
                    }
                    currentWord = null;
                    currentClues = new ArrayList<>();
                } else if (line.startsWith("Object: ")) {
                    currentWord = line.substring(8).trim();
                } else if (currentWord != null && line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length == 2) {
                        currentClues.add(new String[]{parts[0].trim(), parts[1].trim()});
                    }
                }
            }

            if (currentWord != null) {
                loadedWords.add(currentWord);
                loadedClues.add(currentClues);
            }

        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
            return;
        }

        // Ask for board dimensions
        int rows, cols;
        System.out.print("Enter number of rows: ");
        rows = Integer.parseInt(sc.nextLine());
        System.out.print("Enter number of columns: ");
        cols = Integer.parseInt(sc.nextLine());

        int totalNeeded = rows * cols;

        ArrayList<String> validWords = new ArrayList<>();
        for (int i = 0; i < loadedWords.size(); i++) {
            if (!loadedClues.get(i).isEmpty()) {
                validWords.add(loadedWords.get(i));
            }
        }

        if (loadedWords.size() < totalNeeded) {
            System.out.println("Not enough words in the database. Needed: " + totalNeeded);
            return;
        }

        if (validWords.size() < totalNeeded) {
            System.out.println("Not enough words with clues. Needed: " + totalNeeded + ", Found: " + validWords.size());
            return;
        }

        System.out.println("Sufficient words found. Proceeding with the game...");

        // Initialize the board
        char[][] board = new char[rows][cols];
        String[][] wordBoard = new String[rows][cols];
        boolean[][] answered = new boolean[rows][cols];
        boolean[][] tried = new boolean[rows][cols];

        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < loadedWords.size(); i++) indices.add(i);
        Collections.shuffle(indices);

        int wordIdx = 0;
        for (int i = 0; i < rows; i++) {
            Set<Character> used = new HashSet<>();
            int j = 0;
            while (j < cols && wordIdx < indices.size()) {
                int realIdx = indices.get(wordIdx);
                String word = loadedWords.get(realIdx);
                char startChar = Character.toUpperCase(word.charAt(0));
                if (!used.contains(startChar)) {
                    board[i][j] = startChar;
                    wordBoard[i][j] = word;
                    used.add(startChar);
                    j++;
                }
                wordIdx++;
            }
        }

        // Game loop
        int currentRow = 0;
        boolean playerExited = false;
        boolean playerLost = false;

        while (currentRow < rows) {
            boolean answeredCorrectly = false;
            Set<Character> triedLetters = new HashSet<>();

            while (!answeredCorrectly && triedLetters.size() < cols) {
                // Display the full board with * and -
                System.out.println("\n--- CURRENT BOARD ---");
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        if (answered[i][j]) {
                            System.out.print("* ");
                        } else if (tried[i][j]) {
                            System.out.print("- ");
                        } else {
                            System.out.print(board[i][j] + " ");
                        }
                    }
                    System.out.println();
                }

                System.out.print("Choose a letter in row " + (currentRow + 1) + " or X to exit: ");
                String input = sc.nextLine().trim().toUpperCase();

                if (input.equals("X")) {
                    playerExited = true;
                    break;
                }

                if (input.length() != 1 || !Character.isLetter(input.charAt(0))) {
                    System.out.println("Invalid input. Please enter a single letter.");
                    continue;
                }

                char chosenLetter = input.charAt(0);
                boolean found = false;

                for (int j = 0; j < cols; j++) {
                    if (Character.toUpperCase(board[currentRow][j]) == chosenLetter) {
                        if (answered[currentRow][j]) {
                            System.out.println("You already answered that letter.");
                            found = true;
                            break;
                        }
                        if (tried[currentRow][j]) {
                            System.out.println("You already tried that letter.");
                            found = true;
                            break;
                        }

                        triedLetters.add(chosenLetter);

                        String word = wordBoard[currentRow][j];
                        int wordIndex = loadedWords.indexOf(word);
                        ArrayList<String[]> clues = loadedClues.get(wordIndex);
                        String[] clue = clues.get(new Random().nextInt(clues.size()));

                        System.out.println("Clue: " + clue[0] + ": " + clue[1]);
                        System.out.print("Your answer: ");
                        String answer = sc.nextLine().trim();

                        if (answer.equalsIgnoreCase(word)) {
                            System.out.println("Correct!");
                            answered[currentRow][j] = true;
                            answeredCorrectly = true;
                        } else {
                            System.out.println("Wrong answer.");
                            tried[currentRow][j] = true;
                        }

                        found = true;
                        break;
                    }
                }

                if (!found) {
                    System.out.println("Letter not found in current row.");
                }
            }

            if (playerExited) break;
            if (!answeredCorrectly) {
                playerLost = true;
                break;
            }

            currentRow++;
        }

        // Game result
        if (playerExited) {
            System.out.println("You exited the game. You lost.");
        } else if (playerLost) {
            System.out.println("No correct answers in the row. Game Over.");
        } else {
            System.out.println("Congratulations! You answered all rows correctly. YOU WIN!");
        }

        // Ask to play again
        System.out.print("Do you want to play again? (Y/N): ");
        String again = sc.nextLine().trim().toUpperCase();
        if (again.equals("Y")) gamePhase();
        else System.out.println("Returning to main menu.");
    }
}
