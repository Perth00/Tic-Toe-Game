package ch.view

import ch.util.AudioManager
import ch.view.MainApp
import scalafx.event.ActionEvent
import scalafx.scene.control.{Alert, ButtonType}
import scalafx.scene.control.Alert.AlertType
import scalafxml.core.macros.sfxml
import ch.view.BoardController
import ch.view.MainApp.stage
import scalafx.application.JFXApp.Stage
import scalafx.application.Platform
import scalafx.stage.Stage

@sfxml
class HomepageController(){
  // Initialize the audio manager for playing sound effects
  private val audioManager = new AudioManager()

  // Stage for the dialog window
  var dialogStage : Stage  = null
  // Flag to check if OK button was clicked
  var okClicked = false

  // Function to start the game in two-player mode
  def getStart():Unit={
    audioManager.Click.play()  // Play click sound
    alertInformationMessage()  // Show an information alert
    MainApp.showAddPlayerName()  // Show the add player name screen
  }

  // Function to start the game in single-player mode with a bot
  def getStartWithBot():Unit={
    audioManager.Click.play()  // Play click sound
    alertInformationMessage()  // Show an information alert
    MainApp.showAddPlayerWithBot()  // Show the add player with bot screen
  }

  // Function to close the game with a confirmation dialog
  def closeTheGame(): Unit = {
    audioManager.Click.play()  // Play click sound
    val alert = new Alert(AlertType.Confirmation) {
      initOwner(stage)  // Set the owner of the alert dialog
      title = "Exit Game"
      headerText = "Confirm Exit"
      contentText = "Are you sure you want to exit the game?"
    }
    // Apply custom CSS class to the dialog pane for styling
    val dialogPane = alert.dialogPane()
    dialogPane.getStylesheets.add(getClass.getResource("/Css/alertStyles.css").toExternalForm)
    dialogPane.getStyleClass.add("custom-alert")

    val result = alert.showAndWait()
    result match {
      case Some(ButtonType.OK) =>
        audioManager.Click.play()  // Play click sound again
        println("Closing the game")
        sys.exit(0)  // Exit the application
      case _ =>
        println("Cancel exit")
    }
  }

  // Function to open the play policy screen
  def openPlayPolicy(): Unit = {
    audioManager.Click.play()  // Play click sound
    MainApp.showPlayPolicy()  // Show the play policy screen
  }

  // Function to handle the close button action
  def handleCloseButton(): Unit = {
    audioManager.Click.play()  // Play click sound
    okClicked = true  // Set OK clicked flag to true
    dialogStage.close()  // Close the dialog stage
  }

  // Function to open the player history screen
  def openPlayerHistory(): Unit = {
    audioManager.Click.play()  // Play click sound
    MainApp.showPlayerHistory()  // Show the player history screen
  }

  // Function to show an information message alert
  def alertInformationMessage(): Unit = {
    audioManager.Click.play()  // Play click sound
    val alert = new Alert(AlertType.Information) {
      title = "Start Game"
      headerText = "Starting the Game!"
      contentText = "Game is starting..."
    }
    // Apply custom CSS class to the dialog pane for styling
    val dialogPane = alert.dialogPane()
    dialogPane.getStylesheets.add(getClass.getResource("/Css/alertStyles.css").toExternalForm)
    dialogPane.getStyleClass.add("custom-alert")
    alert.showAndWait()  // Show the alert dialog and wait for user response
  }
}
