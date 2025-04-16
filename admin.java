import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class admin {
    private final int MAX_WORDS = 150;
    private final int MAX_TRIVIA = 10;

    String[] words = new String[MAX_WORDS];
    String[][] triviaRel = new String[MAX_WORDS][MAX_TRIVIA];
    String[][] triviaVal = new String[MAX_WORDS][MAX_TRIVIA];
    int[] triviaCount = new int[MAX_WORDS];

    int wordCount = 0;

    Scanner sc = new Scanner(System.in);

    public void run() {
        while (true) {
            System.out.println("Admin Phase!!");
            System.out.println("1. Add a word");
            System.out.println("2. Add a Trivia");
            System.out.println("3. Modify an Entry");
            System.out.println("4. Delete a Word");
            System.out.println("5. Delete a Clue");
            System.out.println("6. View all Words");
            System.out.println("7. View all Clues");
            System.out.println("8. Export");
            System.out.println("9. Import");
            System.out.println("10. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    addWord();
                    break;
                case "2":
                    addTrivia();
                    break;
                case "3":
                    modifyEntry();
                    break;
                case "4":
                    deleteWord();
                    break;
                case "5":
                    deleteClue();
                    break;
                case "6":
                    viewWords();
                    break;
                case "7":
                    viewClues();
                    break;
                case "8":
                    exportToFile();
                    break;
                case "9":
                    importFromFile();
                    break;
                case "10":
                    return;
                default:
                    System.out.println("Invalid. Try again!");
            }
        }
    }

    // Helper function to find a word
    public int findWord(String word) {
        for (int i = 0; i < wordCount; i++) {
            if (words[i].equalsIgnoreCase(word)) {
                return i;
            }
        }
        return -1;
    }

    // CRUD OPERATIONS FOR WORD ======================================================================
    public void addWord() {
        if (wordCount >= MAX_WORDS) {
            System.out.println("Word limit reached.");
            return;
        }

        System.out.print("Enter the word: ");
        String word = sc.nextLine().trim(); 
        if (word.length() == 0 || findWord(word) != -1) {
            System.out.println("Word already exists or is invalid.");
            return;
        }

        words[wordCount] = word;

        System.out.println("Enter trivia for the word (up to " + MAX_TRIVIA + " entries). Type 'done' to stop:");
        for (int i = 0; i < MAX_TRIVIA; i++) {
            System.out.print("Enter trivia relation for " + word + ": ");
            String relation = sc.nextLine();
            if (relation.equalsIgnoreCase("done")) {
                break;
            }
            triviaRel[wordCount][i] = relation;

            System.out.print("Enter trivia value for " + word + ": ");
            String value = sc.nextLine();
            triviaVal[wordCount][i] = value;

            triviaCount[wordCount]++;
        }

        wordCount++;
        System.out.println("Word added successfully.");
    }
    
    public void deleteWord(){
        if(wordCount == 0){
            System.out.println("No words to delete.");
            return;
        }

        String [] sortedWords = new String[wordCount];
        int [] originalIndex = new int[wordCount];
        for (int i = 0; i < wordCount; i++){
            sortedWords[i] = words[i];
            originalIndex[i] = i;
        }
        
        for (int i = 0; i < wordCount - 1; i++) {
            for (int j = i + 1; j < wordCount; j++) {
                if (sortedWords[i].compareToIgnoreCase(sortedWords[j]) > 0) {
                    String tempWord = sortedWords[i];
                    sortedWords[i] = sortedWords[j];
                    sortedWords[j] = tempWord;
    
                    int tempIndex = originalIndex[i];
                    originalIndex[i] = originalIndex[j];
                    originalIndex[j] = tempIndex;
                }
            }
        }

        System.out.println("=== List of Words in Alphabetical Order ===");
        for (int i = 0; i < wordCount; i++){
            System.out.println((i + 1) + "] " + sortedWords[i]);
        }
        
        System.out.println("Enter the number of the word you want to delete:");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice < 1 || choice > wordCount){
            System.out.println("Invalid choice. Try again.");
            return;
        }

        int indexToDelete = originalIndex[choice - 1];
        System.out.println("Deleting word: " + words[indexToDelete]);
        
        for(int i = indexToDelete; i < wordCount - 1; i++){
            words[i] = words[i + 1];
            triviaCount[i] = triviaCount[i + 1];
            for (int j = 0; j < MAX_TRIVIA; j++){
                triviaRel[i][j] = triviaRel[i + 1][j];
                triviaVal[i][j] = triviaVal[i + 1][j];
            }
        }

        words[wordCount - 1] = null;
        triviaRel[wordCount - 1] = new String[MAX_TRIVIA];
        triviaVal[wordCount - 1] = new String[MAX_TRIVIA];

        wordCount--;
        System.out.println("Word deleted successfully.");
    }

    public void viewWords() {
        if (wordCount == 0) {
            System.out.println("No words to view.");
            return;
        }
    
        String[] sortedWords = new String[wordCount];
        int[] originalIndices = new int[wordCount];
    
        for (int i = 0; i < wordCount; i++) {
            sortedWords[i] = words[i];
            originalIndices[i] = i;
        }
    
        for (int i = 0; i < wordCount - 1; i++) {
            for (int j = i + 1; j < wordCount; j++) {
                if (sortedWords[i].compareToIgnoreCase(sortedWords[j]) > 0) {
                    String tempWord = sortedWords[i];
                    sortedWords[i] = sortedWords[j];
                    sortedWords[j] = tempWord;
    
                    int tempIndex = originalIndices[i];
                    originalIndices[i] = originalIndices[j];
                    originalIndices[j] = tempIndex;
                }
            }
        }
    
        int current = 0;
        String input;
    
        while (true) {
            int wordIndex = originalIndices[current];
    
            System.out.println("\n=== Word " + (current + 1) + " of " + wordCount + " ===");
            System.out.println("Object: " + words[wordIndex]);
    
            for (int i = 0; i < MAX_TRIVIA; i++) {
                if (triviaRel[wordIndex][i] != null) {
                    System.out.println(triviaRel[wordIndex][i] + ": " + triviaVal[wordIndex][i]);
                }
            }
    
            System.out.print("\nOptions: [N]ext | [P]revious | E[x]it : ");
            input = sc.nextLine().trim().toUpperCase();
    
            if (input.equals("N")) {
                if (current < wordCount - 1) 
                    current++;
                else 
                    System.out.println("You're at the last word.");
            } else if (input.equals("P")) {
                if 
                    (current > 0) current--;
                else 
                    System.out.println("You're at the first word.");
            } else if 
                (input.equals("X")) {
                return;
            } else {
                System.out.println("Invalid input.");
            }
        }
    }

    // CRUD OPERATIONS FOR TRIVIA ======================================================================
    public void addTrivia() {
        System.out.print("Enter word to add trivia to: ");
        String word = sc.nextLine();

        int index = findWord(word);
        if (index == -1) {
            System.out.println("Word not found.");
            return;
        }

        while (triviaCount[index] < MAX_TRIVIA) {
            System.out.print("Enter trivia relation: ");
            String rel = sc.nextLine().trim();
            System.out.print("Enter trivia value: ");
            String val = sc.nextLine().trim();

            if (!rel.isEmpty() && !val.isEmpty()) {
                triviaRel[index][triviaCount[index]] = rel;
                triviaVal[index][triviaCount[index]] = val;
                triviaCount[index]++;
            }
            
            System.out.print("Add another? (Y/N): ");
            if (!sc.nextLine().equalsIgnoreCase("Y")) {
                break;
            }
        }

        if (triviaCount[index] >= MAX_TRIVIA) {
            System.out.println("MAX BITCHESSSSS");
        }
    }

    // This is not working, it is not deleting the clue
    public void deleteClue() {
        System.out.print("Enter word: ");
        String word = sc.nextLine();
        int index = findWord(word);
        if (index == -1) {
            System.out.println("Word not found.");
            return;
        }
    
        int i;
        for (i = 0; i < triviaCount[index]; i++) {
            System.out.println((i+1) + ". " + triviaRel[index][i] + ": " + triviaVal[index][i]);
        }

        System.out.print("Enter clue number to delete: ");
        int clueNum = Integer.parseInt(sc.nextLine());

        if (clueNum < 1 || clueNum > triviaCount[index]) {
            System.out.println("Invalid clue number.");
            return;
        }

        for (i = clueNum - 1; i < triviaCount[index] - 1; i++) {
            triviaRel[index][i] = triviaRel[index][i + 1];
            triviaVal[index][i] = triviaVal[index][i + 1];
        }

        triviaCount[index]--;
        System.out.println("Clue deleted.");
    }

    // This is not working, it is not viewing the clues correctly
    public void viewClues() {
        System.out.print("Enter word to view clues: ");
        String word = sc.nextLine().trim();

        int index = findWord(word);
        if (index == -1) {
            System.out.println("Tite.");
            return;
        }

        if (triviaCount[index] == 0) {
            System.out.println("No Clues.");
            return;
        }

        System.out.println("Trivia for: " + words[index] + "TItEEE");
        for (int i = 0; i < triviaCount[index]; i++) {
            System.out.println((i+1) + ". " + triviaRel[index][i] + ": " + triviaVal[index]);       
        }
    }

    public void modifyEntry() {
        if (wordCount == 0){
            System.out.println("No words to modify.");
        }

        String [] sortedWords = new String [wordCount];
        int [] originalIndeces = new int [wordCount];

        for (int i = 0; i < wordCount; i++){
            sortedWords[i] = words[i];
            originalIndeces[i] = i;
        }

        for (int i = 0; i < wordCount - 1; i++){
            for (int j = i + 1; j < wordCount; j++){
                if (sortedWords[i].compareToIgnoreCase(sortedWords[j]) > 0){
                    String tempWord = sortedWords[i];
                    sortedWords[i] = sortedWords[j];
                    sortedWords[j] = tempWord;

                    int tempIndex = originalIndeces[i];
                    originalIndeces[i] = originalIndeces[j];
                    originalIndeces[j] = tempIndex;
                }
            }
        }

        System.out.println("=== List of Words in Alphabetical Order ===");
        for (int i = 0; i < wordCount; i++) {
            System.out.println((i + 1) + ". " + sortedWords[i]);
        }

        System.out.print("Enter a Word to Modify: ");
        String targetWord = sc.nextLine().trim();

        int index = findWord(targetWord);
        if (index == -1) {
            System.out.println("Word not found.");
            return;
        }

        String option;
        while (true){
            System.out.println("1. Modify Word");
            System.out.println("2. Modify Trivia");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            option = sc.nextLine();

            switch (option) {
                case "1":
                    System.out.print("Enter new word: ");
                    String newWord = sc.nextLine().trim();

                    if (newWord.length() == 0) {
                        System.out.println("Invalid input.");
                    } else if (findWord(newWord) != -1) {
                        System.out.println("Word already exists.");
                    } else {
                        words[index] = newWord;
                        System.out.println("Word updated.");
                    }
                    break;

                case "2": 
                    System.out.println("Current clues for " + words[index] + ":");
                    for (int i = 0; i < triviaCount[index]; i++) {
                            System.out.println((i + 1) + ". " + triviaRel[index][i]+ ": " + triviaVal[index][i]);
                    }

                    System.out.println("Enter number clue: ");
                    int clueIndex;
                    
                    try {
                        clueIndex = Integer.parseInt(sc.nextLine()) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format.");
                        break;
                    }
                    
                    if (clueIndex < 0 || clueIndex >= triviaCount[index]) {
                        System.out.println("Invalid clue number.");
                        break;
                    }

                    System.out.print("Enter new trivia relation: ");
                    String newRel = sc.nextLine().trim();

                    System.out.print("Enter new trivia value: ");
                    String newVal = sc.nextLine().trim();

                    if (newRel.isEmpty() || newVal.isEmpty()) {
                        System.out.println("Invalid input.");
                    } else {
                        triviaRel[index][clueIndex] = newRel;
                        triviaVal[index][clueIndex] = newVal;
                        System.out.println("Trivia updated.");
                    }
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    public void exportToFile() {
        if (wordCount == 0) {
            System.out.println("No data to export.");
            return;
        }

        System.out.print("Enter filename to export to (max 30 characters): ");
        String filename = sc.nextLine().trim();

        if (filename.length() == 0 || filename.length() > 30) {
            System.out.println("Invalid filename length.");
            return;
        }

        File file = new File(filename);
        List<String> lines = new ArrayList<>();
        Map<String, List<String>> fileData = new LinkedHashMap<>();

        // Step 1: Read file into map of word -> clue list
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                String currentWord = null;
                List<String> currentClues = new ArrayList<>();

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Object: ")) {
                        if (currentWord != null) {
                            fileData.put(currentWord, currentClues);
                        }
                        currentWord = line.substring(8).trim();
                        currentClues = new ArrayList<>();
                    } else if (!line.isEmpty()) {
                        currentClues.add(line);
                    } else {
                        // do nothing for empty lines, they are handled after object
                    }
                }

                if (currentWord != null) {
                    fileData.put(currentWord, currentClues);
                }

            } catch (IOException e) {
                System.out.println("Error reading file.");
                return;
            }
        }

        // Step 2: Merge in-memory trivia into fileData
        Set<String> currentWordsInMemory = new HashSet<>();
        for (int i = 0; i < wordCount; i++) {
            currentWordsInMemory.add(words[i]);
        }

        for (int i = 0; i < wordCount; i++) {
            String word = words[i];
            List<String> clues = fileData.getOrDefault(word, new ArrayList<>());
            Set<String> clueSet = new HashSet<>(clues);

            for (int j = 0; j < triviaCount[i]; j++) {
                String triviaLine = triviaRel[i][j] + ": " + triviaVal[i][j];
                if (!clueSet.contains(triviaLine)) {
                    clues.add(triviaLine);
                    clueSet.add(triviaLine);
                }
            }

            fileData.put(word, clues); // put new or updated word
        }

        // Step 3: Remove any old/renamed words that no longer exist in memory
        fileData.keySet().removeIf(key -> !currentWordsInMemory.contains(key));

        // Step 4: Write everything back
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Map.Entry<String, List<String>> entry : fileData.entrySet()) {
                writer.println("Object: " + entry.getKey());
                for (String clue : entry.getValue()) {
                    writer.println(clue);
                }
                writer.println(); // blank line between objects
            }
            System.out.println("Trivia merged and exported successfully.");
        } catch (IOException e) {
            System.out.println("Error writing to file.");
        }
    }

    public void importFromFile() {
        System.out.print("Enter filename to import from: ");
        String filename = sc.nextLine().trim();

        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("File not found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String currentWord = null;
            String[] tempRel = new String[MAX_TRIVIA];
            String[] tempVal = new String[MAX_TRIVIA];
            int clueCount = 0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    if (currentWord != null) {
                        int existingIndex = findWord(currentWord);

                        boolean addEntry = true;
                        if (existingIndex != -1) {
                            System.out.print("Word '" + currentWord + "' exists. Overwrite? (Y/N): ");
                            String resp = sc.nextLine().trim().toUpperCase();
                            addEntry = resp.equals("Y");

                            if (addEntry) {
                                for (int i = 0; i < MAX_TRIVIA; i++) {
                                    triviaRel[existingIndex][i] = null;
                                    triviaVal[existingIndex][i] = null;
                                }
                            }
                        }

                        if (addEntry) {
                            int index = (existingIndex == -1) ? wordCount++ : existingIndex;
                            words[index] = currentWord;
                            for (int i = 0; i < clueCount; i++) {
                                triviaRel[index][i] = tempRel[i];
                                triviaVal[index][i] = tempVal[i];
                            }
                        }
                    }

                    currentWord = null;
                    clueCount = 0;
                    continue;
                }

                if (line.startsWith("Object: ")) {
                    currentWord = line.substring(8).trim();
                } else if (currentWord != null && line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length == 2 && clueCount < MAX_TRIVIA) {
                        tempRel[clueCount] = parts[0].trim();
                        tempVal[clueCount] = parts[1].trim();
                        clueCount++;
                    }
                }
            }

            if (currentWord != null) {
                int existingIndex = findWord(currentWord);
                boolean addEntry = true;
                if (existingIndex != -1) {
                    System.out.print("Word '" + currentWord + "' exists. Overwrite? (Y/N): ");
                    String resp = sc.nextLine().trim().toUpperCase();
                    addEntry = resp.equals("Y");

                    if (addEntry) {
                        for (int i = 0; i < MAX_TRIVIA; i++) {
                            triviaRel[existingIndex][i] = null;
                            triviaVal[existingIndex][i] = null;
                        }
                    }
                }

                if (addEntry) {
                    int index = (existingIndex == -1) ? wordCount++ : existingIndex;
                    words[index] = currentWord;
                    for (int i = 0; i < clueCount; i++) {
                        triviaRel[index][i] = tempRel[i];
                        triviaVal[index][i] = tempVal[i];
                    }
                }
            }

            System.out.println("Import complete from file: " + filename);
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }
}