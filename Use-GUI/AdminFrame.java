import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class AdminFrame extends JFrame {
    private JTextArea displayArea;
    private JPanel buttonPanel;
    private AdminLogic adminLogic; // Assuming AdminLogic is a class that handles the logic for admin operations

    public AdminFrame(AdminLogic adminLogic) {
        this.adminLogic = adminLogic;

        setTitle("Admin Panel");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1, 10, 10));

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
            JButton btn = new JButton(label);
            btn.setFont(new Font("Arial", Font.PLAIN, 16));
            btn.addActionListener(new AdminButtonListener(label));
            buttonPanel.add(btn);
        }

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(displayArea);

        add(buttonPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);

        setLocationRelativeTo(null); 
    }

    class AdminButtonListener implements ActionListener {
        private String label;

        AdminButtonListener(String label) {
            this.label = label;
        }

        public void actionPerformed(ActionEvent e) {
            switch (label) {
                case "Add Word":
                    String word = JOptionPane.showInputDialog("Enter new word:");
                    if (word != null && !word.isBlank()) {
                        adminLogic.addWordFromGUI(word, AdminFrame.this);
                    }
                    break;
                case "Add Trivia":
                    String wordForTrivia = JOptionPane.showInputDialog("Enter word to add trivia to:");
                    if (wordForTrivia != null && !wordForTrivia.isBlank()) {
                        String trivia = JOptionPane.showInputDialog("Enter trivia for " + wordForTrivia + ":");
                        if (trivia != null && !trivia.isBlank()) {
                            adminLogic.addTriviaFromGUI(wordForTrivia, trivia, AdminFrame.this);
                        }
                    }
                    break;
                case "Modify Word":
                    String oldWord = JOptionPane.showInputDialog("Enter the word to modify:");
                    if (oldWord != null && !oldWord.isBlank()) {
                        String newWord = JOptionPane.showInputDialog("Enter the new word:");
                        if (newWord != null && !newWord.isBlank()) {
                            adminLogic.modifyWordFromGUI(oldWord, newWord, AdminFrame.this);
                        }
                    }
                    break;
                    case "Modify Trivia":
                    String wordForTriviaModification = JOptionPane.showInputDialog("Enter the word to modify trivia for:");
                    if (wordForTriviaModification != null && !wordForTriviaModification.isBlank()) {
                        int index = adminLogic.findWord(wordForTriviaModification);
                        if (index == -1) {
                            JOptionPane.showMessageDialog(AdminFrame.this, "Word not found.");
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
                
                        String triviaIndexStr = JOptionPane.showInputDialog(triviaList.toString());
                        if (triviaIndexStr != null && !triviaIndexStr.isBlank()) {
                            try {
                                int triviaIndex = Integer.parseInt(triviaIndexStr) - 1;
                                if (triviaIndex < 0 || triviaIndex >= adminLogic.getTriviaCount(index)) {
                                    JOptionPane.showMessageDialog(AdminFrame.this, "Invalid trivia selection.");
                                    break;
                                }
                
                                String newTrivia = JOptionPane.showInputDialog("Enter the new trivia (format: relation:value):");
                                if (newTrivia != null && !newTrivia.isBlank()) {
                                    adminLogic.modifyTriviaFromGUI(wordForTriviaModification, triviaIndex, newTrivia, AdminFrame.this);
                                }
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(AdminFrame.this, "Invalid input. Please enter a number.");
                            }
                        }
                    }
                    break;
                case "Delete Word":
                    String wordToDelete = JOptionPane.showInputDialog("Enter the word to delete:");
                    if (wordToDelete != null && !wordToDelete.isBlank()) {
                        adminLogic.deleteWordFromGUI(wordToDelete, AdminFrame.this);
                    }
                    break;
                case "Delete Trivia":
                    String wordForTriviaDeletion = JOptionPane.showInputDialog("Enter the word to delete trivia from:");
                    if (wordForTriviaDeletion != null && !wordForTriviaDeletion.isBlank()) {
                        String triviaToDelete = JOptionPane.showInputDialog("Enter the trivia to delete:");
                        if (triviaToDelete != null && !triviaToDelete.isBlank()) {
                            adminLogic.deleteTriviaFromGUI(wordForTriviaDeletion, triviaToDelete, AdminFrame.this);
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
                    String exportFilename = JOptionPane.showInputDialog("Enter filename to export data:");
                    if (exportFilename != null && !exportFilename.isBlank()) {
                        adminLogic.exportData(exportFilename, AdminFrame.this);
                    }
                    break;
                case "Import Data":
                    String importFilename = JOptionPane.showInputDialog("Enter filename to import data:");
                    if (importFilename != null && !importFilename.isBlank()) {
                        adminLogic.importData(importFilename, AdminFrame.this);
                    }
                    break;
                case "Back to Main Menu":
                    dispose(); // Close the AdminFrame
                    new MainMenu().setVisible(true); // Assuming MainFrame is the main menu frame
                    break;
                default:
                    JOptionPane.showMessageDialog(AdminFrame.this, "Invalid option selected.");
            }
        }
    }

    public void appendToDisplay(String message) {
        displayArea.append(message + "\n");
    }

    public static void main(String[] args) {
        // Assuming AdminLogic is implemented and handles the backend logic
        AdminLogic adminLogic = new AdminLogic();
        SwingUtilities.invokeLater(() -> new AdminFrame(adminLogic).setVisible(true));
    }
}