package ch.model

// Abstract class for a game participant (player or bot)
abstract class GameParticipant(val name: String, val symbol: Char) {
  // Abstract method to make a move on the game board
  // Subclasses must implement this method
  def makeMove(board: Array[Array[String]]): (Int, Int)
}

// Case class for a human player
case class HumanPlayer( _name: String,  _symbol: Char) extends GameParticipant(_name, _symbol) {
  // returns (-1, -1) because human players do not use this method to make their moves.
  // They interact via the user interface, and their moves are handled through UI events.
  override def makeMove(board: Array[Array[String]]): (Int, Int) = {
    // Return an invalid move as a placeholder
    (-1, -1)
  }
}

// Case class for a bot player
case class BotPlayer( _name: String = "Alphago",  _symbol: Char = 'O') extends GameParticipant(_name, _symbol) {
  // Bot makes a random move on the game board
  override def makeMove(board: Array[Array[String]]): (Int, Int) = {
    // Find all empty cells on the board
    val emptyCells = for {
      i <- 0 until 3
      j <- 0 until 3
      if board(i)(j) == ""
    } yield (i, j)

    // Randomly select an empty cell if available
    if (emptyCells.nonEmpty) {
      emptyCells(scala.util.Random.nextInt(emptyCells.size))
    } else {
      // returning (-1, -1) means that no valid moves are available for the bot.
      (-1, -1)
    }
  }
}
