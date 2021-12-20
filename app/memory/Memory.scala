package memory

import models.{Movie, Order, Room, Screening}

import java.util.{Calendar, Date, GregorianCalendar}
import scala.collection.mutable

object Memory extends Functions {
  val movies: mutable.Map[Int, Movie] = mutable.Map.empty[Int, Movie]

  val rooms: mutable.Map[Int, Room] = mutable.Map.empty[Int, Room]

  val screenings: mutable.Map[Int, Screening] = mutable.Map.empty[Int, Screening]

  val orders: mutable.Map[Int, Order] = mutable.Map.empty[Int, Order]


  val addScreeningToMemory: ((Date, Movie, Room)) => OperationStatus = addThing(screenings)(Screening.apply)

  val addRoomToMemory: Option[String] => OperationStatus = addThing(rooms)(Room.apply)

  val addMovieToMemory: ((String,Int)) => OperationStatus = addThing(movies)(Movie.apply)

  val addOrderToMemory: Unit => OperationStatus = addThing(orders)(Order.apply)

  def getScreeningsInInterval(dateOne: Date, dateTwo: Date): List[Screening] = {
    screenings
      .filter {
        case _ -> a => a.isBetween(dateOne,dateTwo)
      }
      .values
      .toList
  }





  println("a")

}
