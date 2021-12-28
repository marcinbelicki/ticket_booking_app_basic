package models

class Seat(i: Int, sR: Option[SeatRow] = None) {

  val id: Int = i
  private var status: SeatStatus = Available

  val thisSeatRow: Option[SeatRow] = sR

  def copy(seatRow:  Option[SeatRow]): Seat = seatRow match {
    case Some(_) => new Seat(id,seatRow)
    case None => new Seat(id,thisSeatRow)
  }
  override def toString: String = s"${thisSeatRow.map(_.char).getOrElse(" ")} $id"

  def getStatus: SeatStatus = {
    status
  }


  def setReserved(order: Order,id: Int): Unit = {
    status = Reserved(order,id)
  }
  def setTaken(order: Order,price: TicketPrice): Unit = {
    status = Taken(order,price)
  }


  def setFree(): Unit = {
    status match {
      case Taken(_,_) | Reserved(_,_) => status = Available
      case _ => ()
    }
  }

}
