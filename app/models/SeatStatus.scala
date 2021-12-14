package models

trait SeatStatus {
  override def toString: String = this match {
    case Taken => "taken"
    case Available => "available"
    case Taken => "taken"
  }
}

object Taken extends SeatStatus

object Available extends SeatStatus

object Unavailable extends SeatStatus

