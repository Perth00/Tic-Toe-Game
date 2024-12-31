package ch.util

import scala.reflect.ClassTag

// Utility object for board-related operations
object BoardUtils {
  /**
   * board: The game board represented as a 2D array.
   * emptyValue: The value representing an empty cell.
   * T: The type of elements in the board.
   * Returns: True if the board is full, False otherwise.
   */
  // Checks if the board is full (i.e., no empty cells)
  def isBoardFull[T: ClassTag](board: Array[Array[T]], emptyValue: T): Boolean = {
    // Flatten the 2D array into a 1D array and check if all cells are not equal to emptyValue
    board.flatten.forall(cell => cell != emptyValue)
  }

  /**
   * board: The game board represented as a 2D array.
   * winPatterns: A list of winning patterns, where each pattern is a list of (row, column) pairs.
   * symbol: The symbol representing the player's moves.
   * T: The type of elements in the board.
   * Returns: True if the player has won, False otherwise.
   */
  // Checks if a player has won the game
  def checkWinner[T: ClassTag](board: Array[Array[T]], winPatterns: List[List[(Int, Int)]], symbol: T): Boolean = {
    // Check if any win pattern is fully matched with the player's symbol on the board
    winPatterns.exists(pattern => pattern.forall { case (i, j) => board(i)(j) == symbol })
  }

  /**
   * board: The game board represented as a 2D array.
   * winPatterns: A list of winning patterns, where each pattern is a list of (row, column) pairs.
   * symbol: The symbol representing the player's moves.
   * T: The type of elements in the board.
   * Returns: The winning pattern if the player has won, None otherwise.
   */
  // Returns the winning pattern if a player has won the game
  def checkWinnerPattern[T: ClassTag](board: Array[Array[T]], winPatterns: List[List[(Int, Int)]], symbol: T): Option[List[(Int, Int)]] = {
    // Find the winning pattern that is fully matched with the player's symbol on the board
    winPatterns.find(pattern => pattern.forall { case (i, j) => board(i)(j) == symbol })
  }
}
