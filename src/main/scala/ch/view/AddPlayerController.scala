package ch.view

import scalafx.event.ActionEvent
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.TextField
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml
import ch.util.{AudioManager, BoardUtils}

@sfxml
class AddPlayerController(private val player1: TextField, private val player2: TextField) {
  var dialogStage: Stage = null
  var okClicked = false
  private val audioManager = new AudioManager()  // Instance of AudioManager to handle click sounds

  // Function to handle the action when confirming player names
  def confirmPlayerNames(action: ActionEvent): Unit = {
    audioManager.Click.play()  // Play click sound
    if (isInputValid()) {  // Validate input
      val player1Name = player1.text.value.trim
      val player2Name = player2.text.value.trim
      if (player1Name.equalsIgnoreCase(player2Name)) {  // Check if both names are the same
        alertMessage("Please don't enter the same name")  // Show alert if names are the same
        okClicked = false
      } else {
        okClicked = true
        dialogStage.close()  // Close dialog stage if names are valid
        MainApp.showBoard(player1Name, player2Name)  // Show the game board with player names
      }
    }
  }

  // Function to handle the action when cancelling player names
  def cancelPlayerNames(action: ActionEvent): Unit = {
    audioManager.Click.play()  // Play click sound
    okClicked = true
    dialogStage.close()  // Close dialog stage
  }

  // Function to handle the action when confirming player names with a bot
  def confirmPlayerNamesWithBot(action: ActionEvent): Unit = {
    audioManager.Click.play()  // Play click sound
    if (isInputValidForBot()) {  // Validate input for bot mode
      val player1Name = player1.text.value.trim
      if (player1Name.equalsIgnoreCase("Alphago")) {  // Ensure player name is not "Alphago"
        alertMessage("Change one name, do not enter Alphago for both players, since it is for the bot")
        okClicked = false
      } else {
        okClicked = true
        dialogStage.close()  // Close dialog stage if name is valid
        MainApp.showBoard(player1Name, "Alphago")  // Show the game board with player and bot
      }
    }
  }

  // Function to validate player names
  def isInputValid(): Boolean = {
    var errorMessage = ""
    if (nullOrEmpty(player1.text.value))
      errorMessage += "• Please fill in Player 1's name.\n"

    if (nullOrEmpty(player2.text.value))
      errorMessage += "• Please fill in Player 2's name.\n"

    if (player1.text.value.equalsIgnoreCase("Alphago") || player2.text.value.equalsIgnoreCase("Alphago")) {
      errorMessage += "• Change one name, do not enter Alphago for both players, since it is for the bot\n"
    }

    if (errorMessage.isEmpty) {
      true  // Return true if no errors
    } else {
      alertMessage(errorMessage)  // Show alert message if there are errors
      false  // Return false to indicate input validation failed
    }
  }

  // Function to validate player name for bot mode
  def isInputValidForBot(): Boolean = {
    var errorMessage = ""
    if (nullOrEmpty(player1.text.value))
      errorMessage += "• Please fill in Player 1's name.\n"

    if (errorMessage.isEmpty) {
      true  // Return true if no errors
    } else {
      alertMessage(errorMessage)  // Show alert message if there are errors
      false  // Return false to indicate input validation failed
    }
  }

  // Function to show alert messages
  def alertMessage(message: String): Unit = {
    val alert = new Alert(AlertType.Error) {
      initOwner(dialogStage)
      title = "Invalid Fields"
      headerText = "Please correct the following errors:"
      contentText = message
    }

    // Apply custom CSS class to the dialog pane for styling
    val dialogPane = alert.dialogPane()
    dialogPane.getStylesheets.add(getClass.getResource("/Css/alertStyles.css").toExternalForm)
    dialogPane.getStyleClass.add("custom-alert")
    // Show the alert dialog and wait for user response
    alert.showAndWait()
  }

  // Function to check if a string is null or empty
  private def nullOrEmpty(x: String): Boolean = x == null || x.trim.isEmpty
}
