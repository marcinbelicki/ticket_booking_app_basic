package models

trait SeatStatus {
  override def toString: String = this match {
    case Taken(_, _) => "taken"
    case Available => "available"
  }

  def toClass(order: Option[Order]): String = (this, order) match {
    case (Taken(_, _), _) => "taken"
    case (Available, _) => "available"
    case (Reserved(a, _), Some(o)) if a == o => "reserved"
    case _ => "taken"

  }

}


case class Taken(order: Order, ticketPrice: TicketPrice) extends SeatStatus

case object Available extends SeatStatus

case class Reserved(order: Order, id: Int) extends SeatStatus

