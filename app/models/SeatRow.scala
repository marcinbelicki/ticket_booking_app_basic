package models

import memory.{Failure, OperationStatus, Success}

class SeatRow(i: Int, s: Array[Seat], scr: Option[Screening] = None){

  val id: Int = i

  val char: Char = (65+id).toChar

  val seats: Array[Seat] = s.map(_.copy(Some(this)))

  val screening: Option[Screening] = scr
  private val liftedSeats: Int => Option[Seat] = seats.lift

  def copy(screening: Screening)= new SeatRow(id,seats.map(_.copy(None)),Some(screening))

  def reserveSeat(id: Int,order: Order): OperationStatus = {
    liftedSeats(id) match {
      case Some(seat) => seat.getStatus match {
        case Available =>
          order.addSeat(seat) match {
            case Success(id: Int) =>
              seat.setReserved(order,id)
              Success("Seat was successfully reserved")
            case Failure(_) => Failure("Seat already taken")
          }


        case Taken =>
          Failure("Seat already taken")
        case Reserved(`order`,id) =>
          seat.setFree()
          order.removeSeat(id)
          Success("Seat was successfully unreserved")
        case _ =>
          Failure("Seat already taken")
      }
      case None =>
        Failure("No such a seat")
    }
  }



}
