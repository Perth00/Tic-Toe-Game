# Tic-Toe-Game
This project is a modern implementation of the classic Tic-Tac-Toe game, designed using Scala (version 2.19.12) and the JavaFX framework for a graphical user interface (GUI). By leveraging FXML, the declarative XML-based interface design tool for JavaFX, the project ensures clean separation between the UI layout and the application logic.

This project implements a classic Tic-Tac-Toe game using Scala, JavaFX, and FXML. It features a user-friendly graphical interface and supports two-player and single-player (with a bot) gameplay modes. The game is designed to showcase interactive features, modular design, and database integration for player history management.

Features
Game Modes:

Two-Player Mode: Play locally with another user.
Single-Player Mode: Play against a bot powered by simple AI.
Dynamic Game Board:

Built using JavaFX GridPane and FXML.
Displays real-time updates based on player actions.
Player Management:

Add new players and validate names via AddPlayerController.
Store and retrieve player win/loss history from the database.
Bot Gameplay:

The bot makes strategic moves using available empty spaces.
Audio Effects:

Integrated sound effects for button clicks, game actions, and alerts using AudioManager.
Database Integration:

Player records (win/loss history) are stored in a Derby database.
Automatic initialization and updates of player records.
Timer System:

Turn timer encourages quick moves and auto-moves when time runs out.
Game Policies:

Customizable rules and policies for gameplay.
File Structure
Controllers:

HomepageController.scala: Manages the game start screen and mode selection.
AddPlayerController.scala: Handles player name input and validation.
BoardController.scala: Main game logic and interaction with the game board.
PlayerHistoryController.scala: Displays player history using a TableView.
Models:

Player.scala: Represents player details and interacts with the database.
GameParticipant.scala: Abstract class for game participants (human or bot).
Utilities:

AudioManager.scala: Plays sound effects for user interactions.
BoardUtils.scala: Provides helper functions to check for a winner or a full board.
DateUtil.scala: Formats and parses dates used in player history.
Database.scala: Configures and manages the Derby database.
FXML and CSS:

FXML files define the layout of various screens.
CSS files provide custom styling for alerts and dialogs.
How to Run
Setup Environment:

Ensure you have IntelliJ IDEA and the Scala plugin installed.
Install SBT for dependency management.
Ensure JavaFX is correctly configured.
Clone and Import:

Clone this repository to your local machine.
Open the project in IntelliJ IDEA.
Add Dependencies:

Verify the build.sbt file includes the necessary dependencies for ScalaFX, JavaFX, and ScalikeJDBC.
Run the Application:

Compile and run the MainApp.scala file.
The game will launch, displaying the homepage.
Database Initialization:

The Derby database initializes automatically on the first run.
Player data is saved and updated in the PlayerRecord table.
Gameplay Instructions
Start the Game:

Choose between Two-Player or Single-Player mode.
Enter player names (ensure names are unique).
Game Mechanics:

Players take turns marking the 3x3 grid.
The game ends when:
A player aligns three symbols in a row, column, or diagonal.
The board is full (draw).
View Player History:

Check win/loss records for all players via the Player History section.
Technology Stack
Programming Language: Scala 2.19.12
UI Framework: JavaFX with FXML
Database: Derby (via ScalikeJDBC)
Audio: Media integration using ScalaFX.
Future Enhancements
Add AI difficulty levels for bot gameplay.
Implement multiplayer support over the network.
Expand player history with detailed game records.
Introduce customizable themes and board sizes.
