package models

import scala.annotation.tailrec

protected class TicketPrice(grosze: Int) {

  val value: Int = grosze

  override def toString: String = {
    @tailrec
    def Helper(list: List[Char], string: String): String = {
      list match {
        case a :: b :: Nil => s"$string,$a$b PLN"
        case head :: tail => Helper(tail, s"$string$head")
      }
    }

    value.toString.toList match {
      case a :: b :: Nil => s"0,$a$b PLN"
      case a :: Nil => s"0,0$a PLN"
      case c => Helper(c, "")
    }
  }

  def +(that: TicketPrice): TicketPrice = new TicketPrice(that.value + value)
}

case object Adult extends TicketPrice(2500)

case object Student extends TicketPrice(1800)

case object Child extends TicketPrice(1250)

case object Zero extends TicketPrice(0)


