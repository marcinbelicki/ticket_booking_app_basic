package memory

abstract class OperationStatus[A](mess: A) {
  val message: A = mess

  def toAny: OperationStatus[Any] = this match {
    case Success(_) => Success[Any](message)
    case Failure(_) => Failure[Any](message)
  }
}

case class Success[A]( override val message: A) extends OperationStatus[A](message)

case class Failure[A]( override val message: A) extends OperationStatus[A](message)