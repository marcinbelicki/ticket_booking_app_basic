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
    Success(id)
  }

  def groupAndSortByParameter1[A, T](a: List[A],l: List[A => T], sep: Int,g: A => Int)(implicit ev$1: T => Ordered[T]): List[(Int,T,Option[Int])] = {
    l match {
      case f::Nil =>
        a
          .map(s => (f(s),s))
          .sortBy(_._1)
          .map {
            case (t: T, head: A) =>
              (sep,t,Some(g(head)))
          }
      case f::tail =>
        a
          .groupBy(f)
          .toList
          .sortBy(_._1)
          .flatMap {
            case (t: T, b) =>
              (sep,t,None)::groupAndSortByParameter1(b,tail,sep+1,g)
          }
    }

  }





}
