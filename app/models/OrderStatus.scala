package models

trait OrderStatus

case object Open extends OrderStatus

case class Finalized(firstName: String, surName: String) extends OrderStatus