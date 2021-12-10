package memory

trait OperationStatus


case class Success(message: String) extends OperationStatus

case class Failure(message: String) extends OperationStatus