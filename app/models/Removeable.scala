package models

import memory.{Failure, OperationStatus, Success}

import scala.collection.mutable

trait Removeable {

  protected def removeThing[A](map: mutable.Map[Int, A])(id: Int): OperationStatus[String] = {
    map.get(id) match {
      case Some(a) =>
        map -= id
        Success(s"$a removed")
      case _ =>
        Failure(s"$id not found")
    }
  }

}
