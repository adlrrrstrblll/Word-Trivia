# 📘 Word Trivia Game

## 📌 Overview

**Word Trivia** is a fun educational game where players answer trivia questions based on a board of randomly arranged letters. Each letter corresponds to a word, and players must guess the word using a provided clue. The game challenges players to guess at least one correct word per row to proceed. A full incorrect row ends the game.

This game was developed as an **Updated Machine Project** for **CCPROG2** at **De La Salle University**, where we implemented the project in **Java** instead of the usual **C programming language**. The project features a modern, responsive graphical user interface with sleek animations and visual effects.

---

## 🎨 UI Features

- **Modern Dark Theme** - Elegant dark mode interface with custom colors
- **Animated Elements** - Smooth transitions and hover effects
- **Custom Fonts** - Using Roboto font for improved readability
- **Responsive Design** - UI elements scale with window size
- **Visual Feedback** - Color-coded responses for game interactions
- **Gradient Backgrounds** - Subtle gradients with optional background image support

---

## 🕹️ Game Features

### 🎮 Game Phase
- Choose your board size (min: 3x3, max: 10x10).
- A beautifully designed tile board with rounded letter tiles.
- Each tile represents the **first letter** of a word with smooth hover effects.
- Interactive clue system - click the same tile again for additional clues.
- Stylish input area for answers with instant visual feedback.
- Color-coded results: ✅ green for correct answers, ❌ red for incorrect ones.
- At least **one correct word per row** is required to move to the next row.
- A full incorrect row results in game over with animated notification.
- The game ends in a **win** with a celebration animation when all rows are completed correctly.
- Modern navigation buttons to play again or return to the main menu.

### 🛠️ Admin Phase
- Sleek admin control panel with intuitive navigation.
- Custom styled dialogs for all interactions.
- Add a new word with up to **10 trivia clues** (`Relation: Value` format).
- Add additional clues to an existing word with real-time feedback.
- Edit words or specific clues with user-friendly interfaces.
- Delete a word or an individual clue with confirmation dialogs.
- View the entire trivia database with syntax highlighting.
- Export trivia data to a `.txt` file with progress indicators.
- Import trivia data from a `.txt` file (with overwrite confirmation).
- Comprehensive error handling with descriptive messages.

---

## 📂 File Format (Trivia Database)

Trivia entries are stored in plain `.txt` format using this layout:

Kind of: Gadget  
Use: Taking Photos  
Color: Black, Silver  
Make: DSLR, Mirrorless  
Brand: Canon, Nikon  

> Each entry begins with `Object:` and is followed by multiple `Relation: Value` lines.  
> **Empty lines** separate each entry.  
> You may include up to **10 clues per word**.

---

## 💻 How to Run

### ✅ Requirements:
- Java 11 or higher
- Any Java IDE (NetBeans, IntelliJ, VS Code) or a terminal with GUI support
- Optional: Background image for enhanced UI experience

### ▶️ Compile and Run
```bash
javac -Xlint:deprecation MainMenu.java
javac MainMenu.java 
java MainMenu
```

---

## 📷 Screenshots

![image](https://github.com/user-attachments/assets/83731f3d-4132-4722-9cdf-322046acb690)
![image](https://github.com/user-attachments/assets/8a0a80aa-b667-4315-b278-2d68cf2d29b4)
![image](https://github.com/user-attachments/assets/a9a3a337-a6e5-4421-a087-ed4f2543a84b)

---

### 👷 Certification
This is to certify that this project is our own work, based on our personal efforts in studying and applying the concepts learned. We have constructed the functions, algorithms, and corresponding code by ourselves. The program was run, tested, and debugged through our own efforts. We further certify that we have not copied in part or whole or otherwise plagiarized the work of other students and/or persons.

- Strebel, Adler Clarence E. - DLSU ID# 12308714
- Pinca, Evan Andrew J. - DLSU ID# <ID NUM>

---
