package models

import memory.{Functions, OperationStatus}

import scala.collection.mutable

class  Order(i: Int) extends Functions{
  val id = i
  val seats: mutable.Map[Int, Seat] = mutable.Map.empty

  val addSeat: Seat => OperationStatus = addThing(seats)((_ => b => b): Int => Seat => Seat)


  def removeSeat(id: Int): Unit =seats-=id
}

object Order {
  def apply(id: Int)(a: Unit): Order = new Order(id)
}