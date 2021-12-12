package memory

import models.{Movie, Room, Screening}


import java.util.{Calendar, Date, GregorianCalendar}
import scala.collection.mutable

object Memory extends Functions {
  val movies: mutable.Map[Int, Movie] = mutable.Map.empty[Int, Movie]

  val rooms: mutable.Map[Int, Room] = mutable.Map.empty[Int, Room]

  val screenings: mutable.Map[Int, Screening] = mutable.Map.empty[Int, Screening]


  val addScreeningToMemory: ((Date, Movie, Room)) => OperationStatus = addThing(screenings)(Screening.apply)

  val addRoomToMemory: Option[String]=> OperationStatus = addThing(rooms)(Room.apply)

  val addMovieToMemory: ((String,Int)) => OperationStatus = addThing(movies)(Movie.apply)


  def getScreeningsInInterval(dateOne: Date, dateTwo: Date): List[Screening] = {
    screenings
      .filter {
        case _ -> a => a.isBetween(dateOne,dateTwo)
      }
      .values
      .toList
  }

  def groupAndSortByParameter[A, T](a: List[A],l: List[A => T], sep: String, href: T => String )(implicit ev$1: T => Ordered[T]): List[String] = {
    l match {
      case f::Nil =>
        a
          .groupBy(f)
          .toList
          .sortBy(_._1)
          .map {
            case (t: T, _) =>
              s"$sep${href(t)}"
          }
      case f::tail =>
        a
          .groupBy(f)
          .toList
          .sortBy(_._1)
          .map {
            case (t: T, b) =>
              (s"$sep$t"::groupAndSortByParameter(b,tail,s"  $sep",href)).mkString("\n")
          }
    }

    }

  def groupAndSortByParameter1[A, T](a: List[A],l: List[A => T], sep: Int, href: T => String )(implicit ev$1: T => Ordered[T]): List[(Int,T)] = {
    l match {
      case f::Nil =>
        a
          .groupBy(f)
          .toList
          .sortBy(_._1)
          .map {
            case (t: T, _) =>
              (sep,t)
          }
      case f::tail =>
        a
          .groupBy(f)
          .toList
          .sortBy(_._1)
          .flatMap {
            case (t: T, b) =>
              (sep,t)::groupAndSortByParameter1(b,tail,sep+1,href)
          }
    }

  }

  println("a")

}
