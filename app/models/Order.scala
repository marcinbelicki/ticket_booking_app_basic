package models

import memory.{Failure, Functions, OperationStatus, Success}
import play.api.mvc.{Action, AnyContent, Request}

import scala.collection.mutable
import scala.util.matching.Regex

class  Order(i: Int) extends Functions{
  val id: Int = i
  val seats: mutable.Map[Int, Seat] = mutable.Map.empty

  val addSeat: Seat => OperationStatus[Int] = addThing(seats)((_ => b => b): Int => Seat => Seat)


  private var status: OrderStatus = Open

  private val fname: Regex = """\p{Lu}\p{L}{2,}""".r
  private val lname: Regex = """\p{Lu}\p{L}{2,}([-]\p{Lu}\p{L}{2,})?""".r

  private val fnameFunction: String => OperationStatus[String] = {
      case fname @ fname() => Success(fname)
      case fname => Failure(fname)
    }
  private val lnameFunction: String => OperationStatus[String] = {
      case lname @ lname(_) => Success(lname)
      case lname => Failure(lname)
   }

  def finalizeUltimately(firstName: String, surName: String): OperationStatus[List[String]] = {
    List(firstName,surName)
      .zip(List(fnameFunction,lnameFunction))
      .map {
        case (a,f) => f(a)
      } match {
      case l @ List(Success(firstName),Success(surName)) =>
        status = Finalized(firstName,surName)
        Success(l.map(_.message))
      case l =>
        Failure(
          l.collect {
            case Failure(message) => message
          }
        )
    }
  }

  def removeSeat(id: Int): Unit =seats-=id

  def finalizeOrder: Map[Screening,(Boolean,OperationStatus[List[Seat]])] = {
    seats
      .values
      .toList
      .groupBy(_.thisSeatRow.get.screening.get)
      .map{
        case screening -> seatList =>
          screening -> {
            screening.checkCondition(this) match {
              case Success(_) => (screening.plusFifteen,Success(seatList))
              case Failure(list) => (screening.plusFifteen,Failure(list))
            }
          }
      }

  }
}

object Order {
  def apply(id: Int)(a: Unit): Order = new Order(id)
}