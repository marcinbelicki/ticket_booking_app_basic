package memory

import models.{Movie, Order, Room, Screening}

import java.util.Date
import scala.collection.mutable

object Memory extends Functions {
  val movies: mutable.Map[Int, Movie] = mutable.Map.empty[Int, Movie]

  val rooms: mutable.Map[Int, Room] = mutable.Map.empty[Int, Room]

  val screenings: mutable.Map[Int, Screening] = mutable.Map.empty[Int, Screening]

  val orders: mutable.Map[Int, Order] = mutable.Map.empty[Int, Order]


  val addScreeningToMemory: ((Date, Movie, Room)) => OperationStatus[Int] = addThing(screenings)(Screening.apply)

  val addRoomToMemory: Option[String] => OperationStatus[Int] = addThing(rooms)(Room.apply)

  val addMovieToMemory: ((String,Int)) => OperationStatus[Int] = addThing(movies)(Movie.apply)

  val addOrderToMemory: Unit => OperationStatus[Int] = addThing(orders)(Order.apply)

  def getScreeningsInInterval(dateOne: Date, dateTwo: Date): List[Screening] = {
    screenings
      .filter {
        case _ -> a => a.isBetween(dateOne,dateTwo)
      }
      .values
      .toList
  }

}
