package models

import memory.{Failure, Functions, OperationStatus, Success}

import scala.collection.mutable

class  Order(i: Int) extends Functions{
  val id: Int = i
  val seats: mutable.Map[Int, Seat] = mutable.Map.empty

  val addSeat: Seat => OperationStatus[Int] = addThing(seats)((_ => b => b): Int => Seat => Seat)


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