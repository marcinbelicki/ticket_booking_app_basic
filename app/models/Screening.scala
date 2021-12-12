package models

import memory.Memory.screenings
import memory.{Failure, OperationStatus, Success}

import java.util.{Calendar, Date}
class Screening(i: Int)(data: (Date, Movie, Room)) extends Removeable {

  val (date,movie,room): (Date, Movie, Room)  = data

  private val id = i

  private val start: Date = date

  private val end: Date = movie.movieEnds(start)




  override def toString: String = {
    s"Screening: $room, $movie, $date"
  }



  def isBetween(dateOne: Date, dateTwo: Date): Boolean = {
    val plusFifteen: Calendar = Calendar.getInstance()
    plusFifteen.add(Calendar.MINUTE,15)
    val a = List(
      plusFifteen.getTime,
      dateOne,
      date,
      dateTwo
    )
      .zipWithIndex
      .sortBy(_._1)
      .map(_._2)
      .drop(2)
      .head
      .equals(2)

    println(screenings)
    a
  }

  def remove: OperationStatus = {
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

}


object Screening {
  def apply(id: Int)(data: (Date, Movie, Room)) = new Screening(id)(data)
}
