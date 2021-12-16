package models

class Seat(i: Int) {

  val id: Int = i
  private var status: SeatStatus = Available

  def copy = new Seat(id)
  override def toString: String = s"$id"

  def getStatus: SeatStatus = {
    status
  }

  def setTaken(): Unit = {
    status = Taken
  }

  def setUnavailable(): Unit = {
    status match {
      case Available => status = Unavailable
      case _ => ()
    }
  }

  def setAvailable(): Unit = {
    status match {
      case Unavailable => status = Available
      case _ => ()
    }
  }

  def setFree(): Unit = {
    status match {
      case Taken => status = Available
      case _ => ()
    }
  }

}
