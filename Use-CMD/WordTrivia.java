import java.util.Scanner;

public class WordTrivia {
    final int MAX_WORDS = 150;
    final int MAX_TRIVIA = 10;

    String[] words = new String[MAX_WORDS];
    String[][] triviaRel = new String[MAX_WORDS][MAX_TRIVIA];
    String[][] triviaVal = new String[MAX_WORDS][MAX_TRIVIA];

    static int wordCount = 0;

    Scanner sc = new Scanner(System.in);

    public void run() {
        while (true) {
            System.out.println("Main Menu!!");
            System.out.println("1. Admin Phase");
            System.out.println("2. Game Phase");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    adminPhase();
                    break;
                case "2":
                    gamePhase();
                    break;
                case "3":
                    System.out.println("Exiting the program.");
                    return;
                default:
                    System.out.println("Invalid. Try again!");
            }
        }
    }

    private void adminPhase() {
        admin admin = new admin(); 
        admin.run();
    }

    private void gamePhase() {
        Game game = new Game(); 
        game.gamePhase();
    }

    public static void main(String[] args) {
        new WordTrivia().run();
    }
}