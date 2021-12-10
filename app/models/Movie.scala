package models


import memory.Memory.{movies, rooms, screenings}
import memory.{Failure, OperationStatus, Success}

import java.util.{Calendar, Date}
import scala.collection.mutable


class Movie(i: Int)(data: (String,Int)) extends Removeable {

  private val id = i


  val (title, durationMinutes): (String,Int) = data


  override def toString: String = s"Movie: $title"


  def movieEnds(date: Date): Date = {
    val calendar = Calendar.getInstance()
    calendar.setTime(date)
    calendar.add(Calendar.MINUTE,durationMinutes)
    calendar.getTime
  }

  def remove: OperationStatus ={
    val messageFromScreenings = screenings.filter{
      case _ -> screening if screening.movie == this => true
      case _ => false
    }.values
      .toList
      .map(_.remove.toString)
      .mkString("\n")
    removeThing(movies)(id) match {
      case Success(message) =>
        Success(s"$message\n$messageFromScreenings")
      case f @ Failure(_) => f
    }
  }

}




object Movie {
  def apply(id: Int)(data:  (String,Int)) = new Movie(id)(data)
}





