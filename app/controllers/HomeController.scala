package controllers

import memory.Memory._
import memory.{Failure, Success}
import models.{Screening, Seat}
import play.api.mvc._

import java.util.{Calendar, GregorianCalendar}
import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */



   def finalizeUltimately: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
     request
       .session
       .get("orderid") match {
       case Some(id) =>
         val idInt = id.toInt
         orders.get(idInt) match {
           case Some(order) =>

               order.finalizeUltimately(request.body.asFormUrlEncoded.get) match {
               case Success(message) =>
                 order.finalizeOrder
                 .filter {
                   case (_,(_,Failure(_)))  => true
                   case _ => false
                 }.toList match {
                   case Nil => Future(Ok(views.html.ultimateFinalization(message++order.groupToScreeningsWithExpirationDate)))
                   case (screening,_)::_ => finalizeOrder(screening.id).apply(request)
                 }
               case Failure(message) =>
                 finalizeOrder(order.seats.head._2.thisSeatRow.get.screening.get.id,message).apply(request)
             }
           case None => Future(Ok("Page not found"))
         }
       case None => Future(Ok("Page not found"))
     }

  }

  def timeInterval(year1: Int,month1: Int,day1: Int,hour1: Int,minutes1: Int,year2: Int,month2: Int,day2: Int,hour2: Int,minutes2: Int): Action[AnyContent] =
    Action { implicit request: Request[AnyContent] =>
      val from = new GregorianCalendar(year1, month1 - 1, day1, hour1, minutes1).getTime
      val to = new GregorianCalendar(year2, month2 - 1, day2, hour2, minutes2).getTime
      val screeningsInInterval = getScreeningsInInterval(from,to )

      val k: Screening => String = s => s.movie.title
      val f: Screening => String = s => String.format("%01d", s.getDay(Calendar.YEAR))
      val g: Screening => String = s => String.format("%02d", s.getDay(Calendar.MONTH) + 1)
      val h: Screening => String = s => String.format("%02d", s.getDay(Calendar.DAY_OF_MONTH))
      val i: Screening => String = s => String.format("%02d", s.getDay(Calendar.HOUR_OF_DAY))
      val j: Screening => String = s => String.format("%02d", s.getDay(Calendar.MINUTE))
      val l: Screening => Int = _.id

      val c = groupAndSortByParameter1(screeningsInInterval,List(k,f,g,h,i,j),0,l)

      Ok(views.html.screenings(c))

    }

  def getScreening(id: Int,error: List[String] = Nil): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    screenings.get(id).filter(_.plusFifteen) match {
      case Some(screening) =>

        Ok(views.html.reservescreening(screening,request.session.get("orderid").map(_.toInt).flatMap(orders.get),error))
      case None => Ok("Screening Unavailable")
    }
  }

  def finalizeOrder(screeningId: Int,error: List[String] = Nil): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.session.get("orderid") match {
      case Some(id) =>
        val idInt = id.toInt
        orders.get(idInt) match {
          case Some(order) =>
            val orderSeats = order.finalizeOrder
            orderSeats.filter {
              case (_,(_,Failure(_)))  => true
              case _ => false
            }.toList match {
              case Nil =>
                orderSeats.toList match {
                  case Nil =>
                    getScreening(screeningId,List("No seats were reserved")).apply(request)
                  case _ =>
                    Future(Ok(views.html.screeningFinalization(orderSeats.map{
                      case (screening,(_,Success(list: List[(Int,Seat)]))) =>
                        screening -> list
                    },error)))

                }

              case head::_=>
                val string = head match {
                  case (screening,(_,Failure(list: List[(Int,Seat)]))) =>
                    s"There was a problem with screening $screening - following seats cannot be left free (as long as they're between two reserved seats)"::list.map(_._2.toString)
                }
                getScreening(head._1.id,string).apply(request)
            }
          case None => getScreening(screeningId,List("error")).apply(request)
        }
      case None => getScreening(screeningId,List("error")).apply(request)
    }
  }


  def reserveSeat(screeningId: Int, rowId: Int, seatId: Int ): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    screenings.get(screeningId).filter(_.plusFifteen) match {
      case Some(screening) =>

        request.session.get("orderid") match {
          case Some(id) =>
            val idInt = id.toInt
            orders.get(idInt) match {
              case Some(_) =>
                screening.reserveSeat(rowId,seatId,idInt)
                Redirect(routes.HomeController.getScreening(screeningId))
              case None =>
                addOrderToMemory()  match {
                  case Success(id: Int) =>
                    addOrderToMemory()
                    screening.reserveSeat(rowId,seatId,id)
                    Redirect(routes.HomeController.getScreening(screeningId)).withSession("orderid" -> id.toString)
                  case _ => Ok("Error - couldnt add order to memory")
                }
            }

          case None =>
            addOrderToMemory() match {
              case Success(id: Int) =>
                addOrderToMemory()
                screening.reserveSeat(rowId,seatId,id)
                Redirect(routes.HomeController.getScreening(screeningId)).withSession("orderid" -> id.toString)
              case _ => Ok("Error - couldnt add order to memory")
            }
        }
      case None => Ok("Screening Unavailable")
    }
  }




}
