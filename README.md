# 📘 Word Trivia Game

## 📌 Overview

**Word Trivia** is a fun educational game where players answer trivia questions based on a board of randomly arranged letters. Each letter corresponds to a word, and players must guess the word using a provided clue. The game challenges players to guess at least one correct word per row to proceed. A full incorrect row ends the game.

This game was developed as an **Updated Machine Project** for **CCPROG2** at **De La Salle University**, where we implemented the project in **Java** instead of the usual **C programming language**.

---

## 🕹️ Game Features

### 🎮 Game Phase
- Choose your board size (min: 3x3, max: 10x10).
- A tile board appears with each tile representing the **first letter** of a word.
- A clue appears when a tile is clicked.
- Input your guess — correct answers turn ✅ green, incorrect answers turn ❌ red.
- At least **one correct word per row** is required to move to the next row.
- A full incorrect row results in game over.
- The game ends in a **win** if all rows are completed correctly.
- You can play again or return to the main menu at any time.

### 🛠️ Admin Phase
- Add a new word with up to **10 trivia clues** (`Relation: Value` format).
- Add additional clues to an existing word.
- Edit words or specific clues.
- Delete a word or an individual clue.
- View the entire trivia database (with `Next`, `Previous`, `Exit` navigation).
- Export trivia data to a `.txt` file.
- Import trivia data from a `.txt` file (with overwrite confirmation).

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

### ▶️ Compile and Run
```bash
javac MainMenu.java 
java MainMenu
```
### 👷 Compile and Run
This is to certify that this project is our own work, based on our personal efforts in studying and applying the concepts learned. We have constructed the functions, algorithms, and corresponding code by ourselves. The program was run, tested, and debugged through our own efforts. We further certify that we have not copied in part or whole or otherwise plagiarized the work of other students and/or persons.

- Strebel, Adler Clarence E. - DLSU ID# 12308714
- Pinca, Evan Andrew J. - DLSU ID# <ID NUM>

---
