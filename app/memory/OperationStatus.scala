package memory

trait OperationStatus


case class Success(message: Any) extends OperationStatus

case class Failure(message: String) extends OperationStatus