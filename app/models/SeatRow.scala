package models

import memory.{Failure, OperationStatus, Success}

class SeatRow(i: Int, s: Array[Seat]){

  val id: Int = i

  val seats: Array[Seat] = s.clone()

  val liftedSeats: Int => Option[Seat] = seats.lift

  def reserveSeat(id: Int): OperationStatus = {
    liftedSeats(id) match {
      case Some(seat) => seat.getStatus match {
        case Available =>
          seat.setTaken()
          List(id + 2,id - 2).foreach(i => liftedSeats(i).foreach(_.setUnavailable()))
          List(id + 1,id - 1).foreach(i => liftedSeats(i).foreach(_.setAvailable()))
          Success("Seat was successfully reserved")
        case Taken =>
          Failure("Seat already taken")
        case Unavailable =>
          Failure("Seat unavailable")
      }
      case None =>
        Failure("No such a seat")
    }
  }


}
