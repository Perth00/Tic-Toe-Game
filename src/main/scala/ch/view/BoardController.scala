package ch.view

// Import necessary packages and classes
import scalafx.scene.control.{Alert, Button, ButtonBar, ButtonType, Label}
import scalafx.scene.text.Text
import scalafxml.core.macros.sfxml
import scalafx.event.ActionEvent
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.layout.GridPane
import scalafx.scene.shape.Line
import ch.model.{BotPlayer, GameParticipant, HumanPlayer, Player}
import ch.util.{AudioManager, BoardUtils}
import ch.view.MainApp.stage
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.Stage
import util.DateUtil

import scala.util.Random
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import java.time.{LocalDate, LocalTime}
import java.time.format.DateTimeFormatter

@sfxml
class BoardController(
                       var cell00Text: Text,
                       var cell01Text: Text,
                       var cell02Text: Text,
                       var cell10Text: Text,
                       var cell11Text: Text,
                       var cell12Text: Text,
                       var cell20Text: Text,
                       var cell21Text: Text,
                       var cell22Text: Text,
                       var p1Label: Label,
                       var p2Label: Label,
                       var player1Label: Label,
                       var player2Label: Label,
                       var player1Win: Label,
                       var player1Lose: Label,
                       var player2Win: Label,
                       var player2Lose: Label,
                       var date: Label,
                       var playerTurn: Label,
                       var timerLabel: Label,  // Add the timer label
                       var line1: Line,        // Add the lines
                       var line2: Line,
                       var line3: Line,
                       var line4: Line,
                       var line5: Line,
                       var line6: Line,
                       var line7: Line,
                       var line8: Line
                     ) {
  // Declare button variables for the board cells
  var cell00Button: Button = _
  var cell01Button: Button = _
  var cell02Button: Button = _
  var cell10Button: Button = _
  var cell11Button: Button = _
  var cell12Button: Button = _
  var cell20Button: Button = _
  var cell21Button: Button = _
  var cell22Button: Button = _

  // Declare player variables
  private var player1: GameParticipant = _
  private var player2: GameParticipant = _
  private var currentPlayer: GameParticipant = _
  private var isSinglePlayer: Boolean = false
  // Initialize the AudioManager for sound effects
  private val audioManager = new AudioManager()
  // Timer variables
  private var timer: java.util.Timer = _
  private var timerTask: java.util.TimerTask = _
  private var timeLeft = 15

  // Player objects for player1 and player2
  private var p1: Player = _
  private var p2: Player = _

  // Initialize the game board as a 2D array of strings
  private val board: Array[Array[String]] = Array(
    Array("", "", ""),
    Array("", "", ""),
    Array("", "", "")
  )

  // Define the winning patterns for the game
  private val winPatterns: List[List[(Int, Int)]] = List(
    // Rows
    List((0, 0), (0, 1), (0, 2)),
    List((1, 0), (1, 1), (1, 2)),
    List((2, 0), (2, 1), (2, 2)),
    // Columns
    List((0, 0), (1, 0), (2, 0)),
    List((0, 1), (1, 1), (2, 1)),
    List((0, 2), (1, 2), (2, 2)),
    // Diagonals
    List((0, 0), (1, 1), (2, 2)),
    List((2, 0), (1, 1), (0, 2))
  )


  // Set player names and initialize players
  def setPlayerNames(player1Name: String, player2Name: String): Unit = {

    // Initialize player1 as a human player with symbol 'X'
    player1 = HumanPlayer(player1Name, 'X')
    // Initialize player2; if named "Alphago", create a bot player, otherwise create a human player with symbol 'O'
    player2 = if (player2Name == "Alphago") BotPlayer() else HumanPlayer(player2Name, 'O')
    // Randomly choose the starting player
    currentPlayer = if (Random.nextBoolean()) player1 else player2
    p1Label.setText(player1Name)
    p2Label.setText(player2Name)
    println(s"First move by: ${currentPlayer.name}")
    playerTurn.setText(s"${currentPlayer.name}'s turn")    // Update the UI labels with player names
    player1Label.setText(player1Name)
    player2Label.setText(player2Name)

    // Initialize player objects
    p1 = Player.findOrCreate(player1Label.getText)
    p2 = Player.findOrCreate(player2Label.getText)

    // Set the current date and time
    val currentDate=updateDateTime()
    p1.updateDate(currentDate)
    p2.updateDate(currentDate)

    println(p1.getDate)
    //initial the player 1 and player 2 name only execute the database
    if(p1.isExist){
      println("This name already exist in database")
      player1Win.setText(p1.getWinCount.toString)
      player1Lose.setText(p1.getLoseCount.toString)
      println("Player 1 name is : "+p1.nameS)
    }else{
      println("This name does not exist in database")
      p1.save()
      player1Win.setText(p1.getWinCount.toString)
      player1Lose.setText(p1.getLoseCount.toString)
    }
    if(p2.isExist){
      println("This name already exist in database")
      player2Win.setText(p2.getWinCount.toString)
      player2Lose.setText(p2.getLoseCount.toString)
      println("Player 1 name is : "+p2.nameS)

    }else{
      println("This name does not exist in database")
      p2.save()
      player2Win.setText(p2.getWinCount.toString)
      player2Lose.setText(p2.getLoseCount.toString)
    }


    // Determine if the game is single-player based on whether player2 is a bot
    isSinglePlayer = player2.isInstanceOf[BotPlayer]

    // If the current player is a bot, make the bot move after a 1-second delay
    if (currentPlayer.isInstanceOf[BotPlayer]) {
      Future {
        // Introduce a delay to simulate thinking time for the bot
        Thread.sleep(1000)
        // Execute the bot move on the JavaFX application thread
        Platform.runLater {
          makeBotMove()
        }
      }
    } else {
      startTimer()
    }
  }

  // 2D array to hold Text nodes corresponding to the Tic Tac Toe grid cells
  private val texts: Array[Array[Text]] = Array(
    Array(cell00Text, cell01Text, cell02Text),
    Array(cell10Text, cell11Text, cell12Text),
    Array(cell20Text, cell21Text, cell22Text)
  )

  // Handle button click events on the board
  def handleButtonClick(action: ActionEvent): Unit = {
    // Check if the current player is a human or if the game is not in single-player mode
    if (currentPlayer.isInstanceOf[HumanPlayer] || !isSinglePlayer) {
      // Get the button that was clicked from the action event source
      val button = action.getSource.asInstanceOf[javafx.scene.control.Button]
      // Determine the row and column index of the clicked button using GridPane properties
      val row = GridPane.getRowIndex(button).intValue()
      val col = GridPane.getColumnIndex(button).intValue()
      // Print the row and column of the clicked button for debugging purposes
      println(s"Button clicked at row: $row, col: $col")
      //show click button click
      audioManager.playerClick.play()
      // Handle the move for the determined row and column
      handleMove(row, col)
    }
  }

  // Handle a move on the board
  def handleMove(row: Int, col: Int): Unit = {
    // Check if the selected cell is empty
    if (board(row)(col) == "") {
      // Update the board with the current player's symbol
      board(row)(col) = currentPlayer.symbol.toString
      // Update the UI text for the cell to show the player's symbol
      texts(row)(col).setText(currentPlayer.symbol.toString)
      println(s"Set text for cell at row: $row, col: $col to ${currentPlayer.symbol}")
      // Check if the current player has won the game
      if (BoardUtils.checkWinner(board, winPatterns, currentPlayer.symbol.toString)) {
        showWinnerAlert(currentPlayer.name)  // Display a winner alert if the player has won
      } else if (BoardUtils.isBoardFull(board, "")) {
        showDrawAlert()  // Display a draw alert if the board is full
      } else {
        // Switch to the next player
        switchPlayer()
        // If the new player is a bot, make a bot move after a delay
        if (currentPlayer.isInstanceOf[BotPlayer] && !BoardUtils.checkWinner(board, winPatterns, currentPlayer.symbol.toString) && !BoardUtils.isBoardFull(board, "")) {
          Future {
            // Introduce a delay to simulate thinking time for the bot
            Thread.sleep(1000)
            // Execute the bot move on the JavaFX application thread
            Platform.runLater {
              makeBotMove()
            }
          }
        }
      }
    }
  }

  // Switch to the next player
  private def switchPlayer(): Unit = {
    // Check if the current player is player1; if so, switch to player2, otherwise switch to player1
    currentPlayer = if (currentPlayer == player1) player2 else player1
    // Start the timer for the next player's turn
    startTimer()
    // Print a message to indicate the switch
    println(s"Switched player to ${currentPlayer.name}")
    playerTurn.setText(s"${currentPlayer.name}'s turn")
  }

  // Make a move for the bot
  private def makeBotMove(): Unit = {
    println("Bot is making a move...")
    // Get the row and column indices for the bot's move
    val (row, col) = currentPlayer.makeMove(board)
    // Check if the move is valid (row and col should be non-negative)
    if (row >= 0 && col >= 0) {
      // Execute the move on the JavaFX application thread
      Platform.runLater {
        // Play the move sound effect
        audioManager.moveSound.play()
        // Handle the move on the board
        handleMove(row, col)
      }
    }
  }

  // Show an alert when a player wins
  private def showWinnerAlert(name: String): Unit = {
    if (timer != null) timer.cancel()  // Cancel the timer
    if (name == "Alphago") {
      // Display lose sound effect to player
      audioManager.lose.play()
      p2.addWinCount()  // Increase bot win count
      p1.addLoseCount()  // Increase player lose count
    } else {
      audioManager.success.play()  // Win sound effect to player
      if (p1.nameS != name) {
        p2.addWinCount()  // Increase player2 win count
        p1.addLoseCount()  // Increase player1 lose count
      } else {
        p1.addWinCount()  // Increase player1 win count
        p2.addLoseCount()  // Increase player2 lose count
      }
    }

    // Schedule the alert to be shown on the JavaFX application thread
    Platform.runLater {
      val alert = new Alert(AlertType.Confirmation) {
        initOwner(null)
        title = "Game Over"
        headerText = s"${name} Wins!"
        contentText = if (name == "Alphago") "You lost the game!!" else "Congratulations!"
        buttonTypes = Seq(
          new ButtonType("Play again", ButtonBar.ButtonData.OKDone),
          new ButtonType("Back to Home", ButtonBar.ButtonData.CancelClose)
        )
      }

      // Apply custom CSS class to the dialog pane for styling
      val dialogPane = alert.dialogPane()
      dialogPane.getStylesheets.add(getClass.getResource("/Css/alertStyles.css").toExternalForm)
      dialogPane.getStyleClass.add("custom-alert")
      audioManager.Click.play()

      val result = alert.showAndWait()
      result match {
        case Some(button) if button.text == "Play again" =>
          audioManager.Click.play()
          resetBoard()
        case Some(button) if button.text == "Back to Home" =>
          audioManager.Click.play()
          MainApp.showWelcome()
        case _ =>
          println("Cancel")
      }
    }

    // Display the winning line based on the winning pattern
    BoardUtils.checkWinnerPattern(board, winPatterns, currentPlayer.symbol.toString) match {
      case Some(pattern) => displayWinningLine(pattern)
      case None => // Do nothing
    }
  }

  // Show an alert when the game is a draw
  private def showDrawAlert(): Unit = {
    if (timer != null) timer.cancel()  // Cancel the timer
    Platform.runLater {
      val alert = new Alert(AlertType.Confirmation) {
        initOwner(null)
        title = "Game Over"
        headerText = "It's a Draw!"
        contentText = "Try Again!"
        buttonTypes = Seq(
          new ButtonType("Play again", ButtonBar.ButtonData.OKDone),
          new ButtonType("Back to Home", ButtonBar.ButtonData.CancelClose)
        )
      }

      // Apply custom CSS class to the dialog pane for styling
      val dialogPane = alert.dialogPane()
      dialogPane.getStylesheets.add(getClass.getResource("/Css/alertStyles.css").toExternalForm)
      dialogPane.getStyleClass.add("custom-alert")
      audioManager.Click.play()

      val result = alert.showAndWait()
      result match {
        case Some(button) if button.text == "Play again" =>
          audioManager.Click.play()
          resetBoard()
        case Some(button) if button.text == "Back to Home" =>
          audioManager.Click.play()
          MainApp.showWelcome()
        case _ =>
          println("Cancel")
      }
    }
  }

  // Reset the game board for a new game
  private def resetBoard(): Unit = {
    // update the win and lose count when game is reset
    player1Win.setText(p1.getWinCount.toString)
    player1Lose.setText(p1.getLoseCount.toString)
    player2Win.setText(p2.getWinCount.toString)
    player2Lose.setText(p2.getLoseCount.toString)
    println("Resetting board...")
    startTimer()
    // Iterate over each cell in the 3x3 board
    for (i <- 0 until 3; j <- 0 until 3) {
      // Clear the cell in the board array
      board(i)(j) = ""
      // Clear the text in the corresponding UI element
      texts(i)(j).setText("")
    }
    // Reset the opacity of all lines to make them transparent
    line1.setOpacity(0)
    line2.setOpacity(0)
    line3.setOpacity(0)
    line4.setOpacity(0)
    line5.setOpacity(0)
    line6.setOpacity(0)
    line7.setOpacity(0)
    line8.setOpacity(0)
    // Randomly select the starting player
    currentPlayer = if (Random.nextBoolean()) player1 else player2
    println(s"First move by: ${currentPlayer.name}")
    // If the starting player is a bot, schedule a bot move
    if (currentPlayer.isInstanceOf[BotPlayer]) {
      Future {
        // Introduce a delay to simulate thinking time for the bot
        Thread.sleep(1000)
        // Execute the bot move on the JavaFX application thread
        Platform.runLater {
          makeBotMove()
        }
      }
    }
  }
  // Start the countdown timer for the current player's turn
  private def startTimer(): Unit = {
    if (timer != null) timer.cancel()  // Cancel any existing timer
    timer = new java.util.Timer()  // Initialize a new timer
    timeLeft = 15  // Set the initial countdown time
    timerLabel.setText(timeLeft.toString)  // Update the timer label with the initial time
    timerTask = new java.util.TimerTask {
      def run(): Unit = {
        Platform.runLater {
          timeLeft -= 1  // Decrease the time left by 1 second
          timerLabel.setText(timeLeft.toString)  // Update the timer label with the new time
          if (timeLeft <= 5 && timeLeft > 0) {  // If time left is between 1 and 5 seconds
            audioManager.warningSound.play()  // Play a warning sound
          }
          if (timeLeft <= 0) {  // If time left reaches 0
            audioManager.missClick.play()  // Play a missed click sound
            timer.cancel()  // Cancel the timer
            makeRandomMove()  // Make a random move for the player
          }
        }
      }
    }
    timer.schedule(timerTask, 1000, 1000)  // Schedule the timer task to run every second
  }

  // Make a random move when the timer runs out
  private def makeRandomMove(): Unit = {
    val emptyCells = for {
      row <- board.indices
      col <- board(row).indices
      if board(row)(col) == ""  // Find all empty cells on the board
    } yield (row, col)

    if (emptyCells.nonEmpty) {
      val (row, col) = emptyCells(Random.nextInt(emptyCells.length))  // Select a random empty cell
      handleMove(row, col)  // Handle the move at the selected cell
    }
  }

  // Display the winning line based on the winning pattern
  private def displayWinningLine(pattern: List[(Int, Int)]): Unit = {
    pattern match {
      case List((0, 0), (0, 1), (0, 2)) => line1.setOpacity(1) // Top row
      case List((1, 0), (1, 1), (1, 2)) => line2.setOpacity(1) // Middle row
      case List((2, 0), (2, 1), (2, 2)) => line3.setOpacity(1) // Bottom row
      case List((0, 0), (1, 0), (2, 0)) => line4.setOpacity(1) // Left column
      case List((0, 1), (1, 1), (2, 1)) => line5.setOpacity(1) // Middle column
      case List((0, 2), (1, 2), (2, 2)) => line6.setOpacity(1) // Right column
      case List((0, 0), (1, 1), (2, 2)) => line7.setOpacity(1) // Diagonal from top-left to bottom-right
      case List((2, 0), (1, 1), (0, 2)) => line8.setOpacity(1) // Diagonal from bottom-left to top-right
      case _ => // Do nothing if no matching pattern
    }
  }

  // Function to restart the game with a confirmation dialog.
  def restartThisMatch(): Unit = {
    val alert = new Alert(AlertType.Confirmation) {
      initOwner(stage)
      title = "Restart This Match"
      headerText = "Confirm Restart"
      contentText = "Are you sure you want to restart the match?"
    }
    // Apply custom CSS class to the dialog pane for styling
    val dialogPane = alert.dialogPane()
    dialogPane.getStylesheets.add(getClass.getResource("/Css/alertStyles.css").toExternalForm)
    dialogPane.getStyleClass.add("custom-alert")
    audioManager.Click.play()  // Play click sound
    val result = alert.showAndWait()
    result match {
      case Some(ButtonType.OK) =>
        audioManager.Click.play()  // Play click sound again
        resetBoard()  // Reset the game board
      case _ =>
        println("Cancel")  // Print cancel message
    }
  }

  // Function to close the game with a confirmation dialog.
  def closeTheGame(): Unit = {
    val alert = new Alert(AlertType.Confirmation) {
      initOwner(stage)
      title = "Exit Game"
      headerText = "Confirm Exit"
      contentText = "Are you sure you want to exit the game?"
    }
    // Apply custom CSS class to the dialog pane for styling
    val dialogPane = alert.dialogPane()
    dialogPane.getStylesheets.add(getClass.getResource("/Css/alertStyles.css").toExternalForm)
    dialogPane.getStyleClass.add("custom-alert")
    audioManager.Click.play()  // Play click sound
    val result = alert.showAndWait()
    result match {
      case Some(ButtonType.OK) =>
        audioManager.Click.play()  // Play click sound again
        println("Closing the game")  // Print closing message
        sys.exit(0)  // Exit the application
      case _ =>
        println("Cancel exit")  // Print cancel exit message
    }
  }

  // Function to open the play policy.
  def openPlayPolicy(): Unit = {
    audioManager.Click.play()  // Play click sound
    MainApp.showPlayPolicy()  // Show the play policy screen
  }

  // Update the date and time label
  private def updateDateTime(): LocalDate = {
    val currentDate = LocalDate.now()  // Get the current date
    val currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))  // Get the current time formatted as HH:mm:ss
    date.setText(s"${currentDate.format(DateUtil.DATE_FORMATTER)} $currentTime")  // Set the date and time label text
    currentDate  // Return the current date
  }


  // Reset the game board for a new game
   def backToHomePage(): Unit = {

     val alert = new Alert(AlertType.Confirmation) {
       initOwner(stage)
       title = "Back to Home Page?"
       headerText = "Confirm"
       contentText = "Are you sure you want to go back to Home page?"
     }
     // Apply custom CSS class to the dialog pane for styling
     val dialogPane = alert.dialogPane()
     dialogPane.getStylesheets.add(getClass.getResource("/Css/alertStyles.css").toExternalForm)
     dialogPane.getStyleClass.add("custom-alert")
     audioManager.Click.play()  // Play click sound
     val result = alert.showAndWait()
     result match {
       case Some(ButtonType.OK) =>
         audioManager.Click.play()  // Play click sound again
         println("Go back to home Page")
         // Iterate over each cell in the 3x3 board
         for (i <- 0 until 3; j <- 0 until 3) {
           // Clear the cell in the board array
           board(i)(j) = ""
           // Clear the text in the corresponding UI element
           texts(i)(j).setText("")
         }
         // Reset the opacity of all lines to make them transparent
         line1.setOpacity(0)
         line2.setOpacity(0)
         line3.setOpacity(0)
         line4.setOpacity(0)
         line5.setOpacity(0)
         line6.setOpacity(0)
         line7.setOpacity(0)
         line8.setOpacity(0)
         timer.cancel()
         MainApp.showWelcome()
       case _ =>
         println("Cancel")  // Print cancel exit message
     }

  }
}
