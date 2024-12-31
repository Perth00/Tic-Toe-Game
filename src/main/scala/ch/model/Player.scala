package ch.model

import scalafx.beans.property.{IntegerProperty, ObjectProperty, StringProperty}
import scalikejdbc.DB
import scala.util.{Try, Success, Failure}
import scalikejdbc._
import java.time.LocalDate
import util.Database
import util.DateUtil._

// The Player class represents a player in the database, inheriting from the Database trait for database interactions
class Player(val nameS: String) extends Database {
  var name = new StringProperty(nameS)  // Player's name
  var win = IntegerProperty(0)  // Number of wins
  var lose = IntegerProperty(0)  // Number of losses
  var date = ObjectProperty[LocalDate](LocalDate.now)  // Date of last update

  // Save the player's record to the database
  def save(): Try[Int] = {
    if (!isExist) {  // If player does not exist, insert new record
      Try(DB autoCommit { implicit session =>
        sql"""
          insert into PlayerRecord (name, win, lose, date) values
          (${name.value}, ${win.value}, ${lose.value}, ${date.value.asString})
        """.update.apply()
      })
    } else {  // If player exists, update existing record
      Try(DB autoCommit { implicit session =>
        sql"""
          update PlayerRecord
          set
            win = ${win.value},
            lose = ${lose.value},
            date = ${date.value.asString}
          where name = ${name.value}
        """.update.apply()
      })
    }
  }

  // Delete the player's record from the database
  def delete(): Try[Int] = {
    if (isExist) {  // If player exists, delete the record
      Try(DB autoCommit { implicit session =>
        sql"""
          delete from PlayerRecord where name = ${name.value}
        """.update.apply()
      })
    } else
      throw new Exception("Player does not exist in Database")  // Throw exception if player does not exist
  }

  // Check if the player's record exists in the database
  def isExist: Boolean = {
    DB readOnly { implicit session =>
      sql"""
        select * from PlayerRecord where name = ${name.value}
      """.map(rs => rs.string("name")).single.apply()
    } match {
      case Some(_) => true
      case None => false
    }
  }

  // Get the player's win count from the database
  def getWinCount: Int = {
    DB readOnly { implicit session =>
      sql"""
        select win from PlayerRecord where name = ${name.value}
      """.map(rs => rs.int("win")).single.apply()
    } match {
      case Some(winCount) => winCount
      case None => 0
    }
  }

  // Get the player's loss count from the database
  def getLoseCount: Int = {
    DB readOnly { implicit session =>
      sql"""
        select lose from PlayerRecord where name = ${name.value}
      """.map(rs => rs.int("lose")).single.apply()
    } match {
      case Some(loseCount) => loseCount
      case None => 0
    }
  }

  // Get the player's record date from the database
  def getDate: String = {
    DB readOnly { implicit session =>
      sql"""
        select date from PlayerRecord where name = ${name.value}
      """.map(rs => rs.string("date")).single.apply()
    } match {
      case Some(dateValue) => dateValue
      case None => ""
    }
  }

  // Increment the player's win count and update the record in the database
  def addWinCount(): Try[Int] = {
    val currentWin = getWinCount
    win.value = currentWin + 1
    lose.value = getLoseCount
    date.value = getDate.parseLocalDate
    updatePlayerRecord()
  }

  // Increment the player's loss count and update the record in the database
  def addLoseCount(): Try[Int] = {
    val currentLose = getLoseCount
    lose.value = currentLose + 1
    win.value = getWinCount
    date.value = getDate.parseLocalDate
    updatePlayerRecord()
  }

  // Update the player's record date and update the record in the database
  def updateDate(newDate: LocalDate): Try[Int] = {
    date.value = newDate
    win.value = getWinCount
    lose.value = getLoseCount
    updatePlayerRecord()
  }

  // Update the player's record in the database
  private def updatePlayerRecord(): Try[Int] = {
    Try(DB autoCommit { implicit session =>
      sql"""
        update PlayerRecord
        set
          win = ${win.value},
          lose = ${lose.value},
          date = ${date.value.asString}
        where name = ${name.value}
      """.update.apply()
    })
  }
}

// Companion object for the Player class, containing utility methods for interacting with the database
object Player extends Database {

  // Factory method to create a Player instance
  def apply(
             nameS: String,
             winS: Int,
             loseS: Int,
             dateS: String
           ): Player = {
    new Player(nameS) {
      win.value = winS
      lose.value = loseS
      date.value = dateS.parseLocalDate
    }
  }

  // Initialize the PlayerRecord table in the database
  def initializeTable(): Unit = {
    DB autoCommit { implicit session =>
      sql"""
        create table PlayerRecord (
          id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
          name varchar(64),
          win int,
          lose int,
          date varchar(64)
        )
      """.execute.apply()
    }
  }

  // Retrieve all players from the database
  def getAllPlayers: List[Player] = {
    DB readOnly { implicit session =>
      sql"select * from PlayerRecord".map(rs => Player(
        rs.string("name"),
        rs.int("win"),
        rs.int("lose"),
        rs.string("date")
      )).list.apply()
    }
  }

  // Find a player by name or create a new one if not found
  def findOrCreate(name: String): Player = {
    getPlayerByName(name) match {
      case Some(player) => player
      case None =>
        val newPlayer = new Player(name)
        newPlayer.save()
        newPlayer
    }
  }

  // Retrieve a player by name from the database
  def getPlayerByName(name: String): Option[Player] = {
    DB readOnly { implicit session =>
      sql"select * from PlayerRecord where name = $name".map(rs => Player(
        rs.string("name"),
        rs.int("win"),
        rs.int("lose"),
        rs.string("date")
      )).single.apply()
    }
  }
}
