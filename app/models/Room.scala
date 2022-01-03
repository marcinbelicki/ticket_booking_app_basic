package models


import memory.Memory.{addScreeningToMemory, rooms, screenings}
import memory.{Failure, OperationStatus, Success}

import java.util.Date

class Room(i: Int)(name: Option[String]) extends Removeable {

  private val id = i

  override def toString: String = name match {
    case Some(value) =>
      s"Room $value"
    case _ =>
      s"Room $id"
  }


  private val seats: Array[SeatRow] = Range(0, 10).toArray.map(new SeatRow(_, Range(0, 10).toArray.map(new Seat(_)).clone()))


  private val TheRoom = this

  def copySeats(screening: Screening): Array[SeatRow] = {
    seats.map(_.copy(screening))
  }

  def addScreening(movie: Movie, date: Date): OperationStatus[Any] = {
    val end = movie.movieEnds(date)
    screenings.filter {
      case _ -> screening if screening.room == TheRoom & !screening.checkColision(date, end) => true
      case _ => false
    }
      .values
      .toList
    match {
      case Nil =>
        addScreeningToMemory(date, movie, TheRoom).toAny
      case a =>
        Failure(s"Screening has collision with:\n${a.mkString("\n")}")
    }
  }

  def remove: OperationStatus[String] = {
    val messageFromScreenings = screenings.filter {
      case _ -> screening if screening.room == TheRoom => true
      case _ => false
    }.values
      .toList
      .map(_.remove.toString)
      .mkString("\n")
    removeThing(rooms)(id) match {
      case Success(message) =>
        Success(s"$message\n$messageFromScreenings")
      case f@Failure(_) => f
    }
  }

}


object Room {
  def apply(id: Int)(name: Option[String]) = new Room(id)(name)
}
