package ch.view

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.{scene => jfxs}
import scalafx.collections.ObservableBuffer
import scalafx.stage.{Modality, Stage}
import util.Database
import ch.model.Player
import scalafx.scene.image.Image
import scalafx.scene.media.{Media, MediaPlayer}

object MainApp extends JFXApp {
  Database.setupDB()
  val PlayerDate = new ObservableBuffer[Player]()
  PlayerDate++=Player.getAllPlayers

  // Initialize BGM
  val bgmURL = getClass.getResource("/Media/Game.mp3").toString
  val bgmMedia = new Media(bgmURL)
  val bgmPlayer = new MediaPlayer(bgmMedia)
  bgmPlayer.setCycleCount(MediaPlayer.Indefinite)  // Loop the BGM
  bgmPlayer.setVolume(0.05)  // Adjust volume as needed
  bgmPlayer.play()  // Start playing BGM


  // Transform path of RootLayout.fxml to URI for resource location.
  val rootResource = getClass.getResource("/ch/view/RootLayout.fxml")

  // Initialize the loader object.
  val loader = new FXMLLoader(rootResource, NoDependencyResolver)

  // Load root layout from fxml file.
  loader.load()

  // Retrieve the root component BorderPane from the FXML.
  val roots = loader.getRoot[jfxs.layout.BorderPane]

  // Initialize stage.
  stage = new PrimaryStage {
    title = "Tic Tac Toe"
    icons += new Image(getClass.getResourceAsStream("/img/Tictactoe.png"))

    scene = new Scene {
      root = roots
    }
    width = 1070  // Set the fixed width of the dialog window
    height = 580  // Set the fixed height of the dialog window
    resizable = false  // Fix the window size
  }

  // Function to show the welcome page.
  def showWelcome(): Unit = {
    // Load the FXML file for the welcome page.
    val resource = getClass.getResource("/ch/view/HomePage.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)  // Set the loaded AnchorPane as the center content of the BorderPane.
  }

  // Call showWelcome function to display the welcome page.
  showWelcome()

  // Function to show the add player name dialog.
  def showAddPlayerName(): Unit = {
    // Load the FXML file for the add person dialog.
    val resource = getClass.getResourceAsStream("/ch/view/PlayName.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource)
    val addPersonPage = loader.getRoot[jfxs.Parent]
    val control = loader.getController[AddPlayerController#Controller]
    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)  // Set modality of the dialog to application modal. This means it blocks interaction with the main window until the dialog is closed.
      initOwner(stage)  // Set the main stage as the owner of the dialog. it means that the dialog window will pop out and be related to the main window of your application.
      scene = new Scene {
        root = addPersonPage  // Set the loaded Parent node as the root of the Scene.
      }
      resizable = false  // Fix the dialog window size
    }
    control.dialogStage = dialog
    dialog.showAndWait()
    control.okClicked
  }

  // Function to show the player name dialog.
  def showAddPlayerWithBot(): Unit = {
    // Load the FXML file for the add person dialog.
    val resource = getClass.getResourceAsStream("/ch/view/PlayBot.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource)
    val addPersonPage = loader.getRoot[jfxs.Parent]  // Load the root node from the FXML file.
    val control = loader.getController[AddPlayerController#Controller]  // Retrieve the controller associated with the FXML file.
    val dialog = new Stage() {
      // Set modality of the dialog to application modal. This means it blocks interaction with the main window until the dialog is closed.
      initModality(Modality.ApplicationModal)
      // Set the main stage as the owner of the dialog. It means that the dialog window will pop out and be related to the main window of your application.
      initOwner(stage)
      scene = new Scene {
        root = addPersonPage  // Set the loaded Parent node as the root of the Scene.
      }
      resizable = false  // Fix the dialog window size
    }
    control.dialogStage = dialog  // Set the dialog stage in the controller.
    dialog.showAndWait()  // Show the dialog and wait until it is closed before returning.
    control.okClicked  // Return the result of the OK button being clicked.
  }

  // Function to show the game board.
  def showBoard(player1Name: String, player2Name: String): Unit = {
    // Load the FXML file for the game board.
    val resource = getClass.getResource("/ch/view/Board.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    // Retrieve the controller from the FXMLLoader
    val control = loader.getController[BoardController#Controller]
    // Set the player names in the controller.
    control.setPlayerNames(player1Name, player2Name)
    // Load the root node from the FXML file.
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    // Set the loaded root node as the center of the main layout.
    this.roots.setCenter(roots)
  }

  // Function to show the play policy page.
  def showPlayPolicy(): Unit = {
    // Load the FXML file for the add person dialog.
    val resource = getClass.getResourceAsStream("/ch/view/PlayPolicy.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource)
    val playPolicy = loader.getRoot[jfxs.Parent]
    val control = loader.getController[HomepageController#Controller]
    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)  // Set modality of the dialog to application modal. This means it blocks interaction with the main window until the dialog is closed.
      initOwner(stage)  // Set the main stage as the owner of the dialog. it means that the dialog window will pop out and be related to the main window of your application.
      scene = new Scene {
        root = playPolicy  // Set the loaded Parent node as the root of the Scene.
      }
      resizable = false  // Fix the dialog window size
    }
    control.dialogStage = dialog
    dialog.showAndWait()
    control.okClicked
  }
  // Function to show the player history page.
  def showPlayerHistory(): Unit = {
    // Load the FXML file for the add person dialog.
    val resource = getClass.getResourceAsStream("/ch/view/PlayerHistory.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource)
    val playerHistory = loader.getRoot[jfxs.Parent]
    val control = loader.getController[PlayerHistoryController#Controller]

    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)  // Set modality of the dialog to application modal. This means it blocks interaction with the main window until the dialog is closed.
      initOwner(stage)  // Set the main stage as the owner of the dialog. it means that the dialog window will pop out and be related to the main window of your application.
      scene = new Scene {
        root = playerHistory  // Set the loaded Parent node as the root of the Scene.
      }
      resizable = false  // Fix the dialog window size
    }
    control.dialogStage = dialog
    dialog.showAndWait()
    control.okClicked  }
}
