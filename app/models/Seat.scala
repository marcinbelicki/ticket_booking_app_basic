package models

class Seat {

  private var status: SeatStatus = Available

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
