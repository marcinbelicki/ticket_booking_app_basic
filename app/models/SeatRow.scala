package models

import memory.{Failure, OperationStatus, Success}

class SeatRow(i: Int, s: Array[Seat]){

  val id: Int = i

  val char: Char = (65+id).toChar

  val seats: Array[Seat] = s

  private val liftedSeats: Int => Option[Seat] = seats.lift

  def copy = new SeatRow(id,seats.map(_.copy))

  def reserveSeat(id: Int): OperationStatus = {
    liftedSeats(id) match {
      case Some(seat) => seat.getStatus match {
        case Available =>
          seat.setTaken()
          List(id + 2,id - 2)
            .zip(List(id + 1,id - 1))
            .foreach{
              case (a,b) =>
                liftedSeats(b).foreach{
                  seat =>
                    seat.getStatus match {
                      case Taken => ()
                      case _ =>
                        liftedSeats(a).foreach(_.setUnavailable())
                        seat.setAvailable()
                    }
                }
            }
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
