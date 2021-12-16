package memory




import scala.annotation.tailrec
import scala.collection.mutable

trait Functions  {

  protected def addThing[A, B](map: mutable.Map[Int, B])(f: Int => A => B)(a: A): OperationStatus = {
    @tailrec
    def Helper(id: Int): Int = {
      map.get(id) match {
        case Some(_) => Helper(id + 1)
        case _ => id
      }
    }

    val id = Helper(0)
    val value = f(id)(a)
    map += id -> value
    Success(s"$value added with id $id")
  }





}
