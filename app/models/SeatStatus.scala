package models

trait SeatStatus {
  override def toString: String = this match {
    case Taken => "taken"
    case Available => "available"
  }

  def toClass(order: Option[Order]): String = (this,order) match {
    case (Taken,_) => "taken"
    case (Available,_) => "available"
    case (Reserved(a,_),Some(o)) if a == o => "reserved"
    case _ => "taken"

  }

}



case object Taken extends SeatStatus

case object Available extends SeatStatus

case class Reserved(order: Order, id: Int) extends SeatStatus

