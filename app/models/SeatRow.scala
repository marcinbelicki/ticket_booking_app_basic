package models

import memory.{Failure, OperationStatus, Success}

class SeatRow(i: Int, s: Array[Seat]){

  val id: Char = (64+i).toChar

  private val seats: Array[Seat] = s

  private val liftedSeats: Int => Option[Seat] = seats.lift

  def reserveSeat(id: Int): OperationStatus = {
    liftedSeats(id) match {
      case Some(seat) => seat.getStatus match {
        case Available =>
          seat.setTaken()
          val f: List[Int] => (Seat => Any) => Unit = l => g => l.foreach(liftedSeats(_).foreach(g))
          f(List(id + 2,id - 2))(_.setUnavailable())
          f(List(id + 1,id - 1))(_.setAvailable())
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
