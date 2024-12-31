package ch.view

import scalafx.stage.Stage
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.control.{TableColumn, TableView}
import scalafxml.core.macros.sfxml
import ch.model.Player
import ch.util.AudioManager
import scalafx.beans.property.{IntegerProperty, ObjectProperty, StringProperty}

import java.time.LocalDate
import util.DateUtil._
import ch.view.MainApp

@sfxml
class PlayerHistoryController(
                               private val playerHistoryTable: TableView[Player],
                               private val nameColumn: TableColumn[Player, String],
                               private val winColumn: TableColumn[Player, Number],
                               private val loseColumn: TableColumn[Player, Number],
                               private val dateColumn: TableColumn[Player, String]
                             ) {

  // Initialize the TableView to display the contents of the PlayerDate list from MainApp
  playerHistoryTable.items = MainApp.PlayerDate

  // Set up the cell value factories for the table columns
  nameColumn.cellValueFactory = { cellData => cellData.value.name }  // Bind name column to the name property of Player
  winColumn.cellValueFactory = { cellData => ObjectProperty(cellData.value.win.value) }  // Bind win column to the win property of Player
  loseColumn.cellValueFactory = { cellData => ObjectProperty(cellData.value.lose.value) }  // Bind lose column to the lose property of Player
  dateColumn.cellValueFactory = { cellData => new StringProperty(cellData.value.date.value.toString) }  // Bind date column to the date property of Player

  // Initialize the audio manager for playing sound effects
  private val audioManager = new AudioManager()
  // Stage for the dialog window
  var dialogStage: Stage = null
  // Flag to check if OK button was clicked
  var okClicked = false

  // Function to close the player history dialog
  def closePlayerHistory(): Unit = {
    audioManager.Click.play()  // Play click sound
    okClicked = true  // Set OK clicked flag to true
    dialogStage.close()  // Close the dialog stage
  }
}
