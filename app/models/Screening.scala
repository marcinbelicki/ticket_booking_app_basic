package models

import memory.Memory.{orders, screenings}
import memory.{Failure, OperationStatus, Success}

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}
import scala.math.Ordered.orderingToOrdered
class Screening(i: Int)(data: (Date, Movie, Room)) extends Removeable {

  val (date,movie,room): (Date, Movie, Room)  = data

  val id: Int = i

  private val start: Date = date

  private val end: Date = movie.movieEnds(start)

  val seats: Array[SeatRow] = room.copySeats(this)


  private val dateformat = new SimpleDateFormat("YYYY-MM-dd HH:mm")

  val formattedDate: String = dateformat.format(date)
  override def toString: String = {
    s"Screening: $movie, $room, ${formattedDate}"
  }



  def  plusFifteen: Boolean = {
    val cal: Calendar = Calendar.getInstance()
    cal.add(Calendar.MINUTE,15)
    date >= cal.getTime
  }

  def isBetween(dateOne: Date, dateTwo: Date): Boolean = {
    plusFifteen && List(
      dateOne,
      date,
      dateTwo
    )
      .zipWithIndex
      .sortBy(_._1)
      .apply(1)
      ._2
      .equals(1)
  }

  def remove: OperationStatus[String] = {
    removeThing(screenings)(id)
  }

  def checkColision(startThat: Date,endThat: Date): Boolean = {
    List(startThat,
      endThat,
      start,
      end
    ).zipWithIndex
      .sortBy(_._1)
      .map(_._2)
    match {
      case List(0,1,_*) | List(_,_,0,1) => true
      case _ => false
    }
  }

  def getDay(field: Int): Int = {
    val cal: Calendar = Calendar.getInstance()
    cal.setTime(date)
    cal.get(field)
  }

  def reserveSeat(rowId: Int, seatId: Int, orderId: Int): OperationStatus[String] = {
    seats.lift(rowId) match {
      case Some(row) =>
        orders.get(orderId) match {
          case Some(order) =>
            row.reserveSeat(seatId,order)
          case _ =>
            Failure("Seat doesn't exist")
        }
      case None =>
        Failure("Seat doesn't exist")
    }
  }

  def checkCondition(order: Order): OperationStatus[List[Seat]] = {
    seats
      .map(_.checkCondition(order))
      .flatMap {
        case Failure(l) => l
        case _ => Nil
      }
      .toList match {
      case Nil => Success(Nil)
      case list => Failure(list)
    }
  }

}


object Screening {
  def apply(id: Int)(data: (Date, Movie, Room)) = new Screening(id)(data)
}
