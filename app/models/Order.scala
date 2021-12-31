package models

import memory.{Failure, Functions, OperationStatus, Success}

import scala.collection.mutable
import scala.util.matching.Regex

class Order(i: Int) extends Functions {
  val id: Int = i
  val seats: mutable.Map[Int, Seat] = mutable.Map.empty

  val addSeat: Seat => OperationStatus[Int] = addThing(seats)((_ => b => b): Int => Seat => Seat)


  private var status: OrderStatus = Open

  private val fname: Regex = """\p{Lu}\p{L}{2,}""".r
  private val lname: Regex = """\p{Lu}\p{L}{2,}([-]\p{Lu}\p{L}{2,})?""".r

  private val fnameFunction: String => OperationStatus[String] = {
    case fname@fname() => Success(fname)
    case fname => Failure(fname)
  }
  private val lnameFunction: String => OperationStatus[String] = {
    case lname@lname(_) => Success(lname)
    case lname => Failure(lname)
  }

  private def groupToScreeningsList = {
    seats
      .values
      .toList
      .groupBy(_.thisSeatRow.get.screening.get)
      .toList
  }

  def groupToScreenings: List[String] = {
    groupToScreeningsList
      .flatMap {
        case (screening: Screening, list: List[Seat]) =>
          screening.movie.toString ::
            screening.room.toString ::
            screening.formattedDate ::
            list.map(_.toString)
      }
  }

  def groupToScreeningsWithExpirationDate: List[String] = {
    groupToScreeningsList
      .flatMap {
        case (screening: Screening, list: List[Seat]) =>
          screening.movie.toString ::
            screening.room.toString ::
            screening.formattedDate ::
            s"Tickets for this screening expire at ${screening.formattedEnd}" ::
            list.map(_.toString)
      }
  }

  def finalizeUltimately(body: Map[String, Seq[String]]): OperationStatus[List[String]] = {
    List("fname", "lname")
      .flatMap(body.get(_).flatMap(_.headOption))
      .zip(List(fnameFunction, lnameFunction))
      .map {
        case (a, b) => b(a)
      } match {
      case l@List(Success(firstName), Success(surName)) =>
        status = Finalized(firstName, surName)
        seats match {
          case mutable.Map.empty => Failure(List("No seats reserved"))
          case _ =>
            seats.foldLeft((Nil.asInstanceOf[List[Seat]], Zero.asInstanceOf[TicketPrice])) {
              case ((seatsWithout, price), seatId -> seat) =>
                body.get(seatId.toString).flatMap(_.headOption) match {
                  case Some("adult") =>
                    seat.setTaken(this, Adult)
                    (seatsWithout, price + Adult)
                  case Some("student") =>
                    seat.setTaken(this, Student)
                    (seatsWithout, price + Student)
                  case Some("child") =>
                    seat.setTaken(this, Child)
                    (seatsWithout, price + Child)
                  case _ =>
                    (seat :: seatsWithout, price)
                }
            } match {
              case (Nil, price) =>
                Success(s"Order finalized with total price $price and full name ${l.map(_.message).mkString(" ")}" :: Nil)
              case (l, _) =>
                Failure("Didn't found prices for following seats" :: l.map(_.toString))
            }

        }

      case _ =>
        Failure(List("Your last name or first name didn't match the criteria"))
    }

  }

  def removeSeat(id: Int): Unit = seats -= id


  def finalizeOrder: Map[Screening, (Boolean, OperationStatus[List[(Int, Seat)]])] = {
    seats
      .toList
      .groupBy(_._2.thisSeatRow.get.screening.get)
      .map {
        case screening -> seatList =>
          screening -> {
            screening.checkCondition(this) match {
              case Success(_) => (screening.plusFifteen, Success(seatList))
              case Failure(list) => (screening.plusFifteen, Failure(list.zipWithIndex.map(a => (a._2, a._1))))
            }
          }
      }

  }
}

object Order {
  def apply(id: Int)(a: Unit): Order = new Order(id)
}