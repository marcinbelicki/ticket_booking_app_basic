package controllers

import memory.Memory.{addOrderToMemory, getScreeningsInInterval, groupAndSortByParameter1, orders, screenings}
import memory.{Failure, Success}
import models.{Screening, Seat}
import play.api.mvc._

import java.net.URLDecoder
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

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
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
    screenings.filter(_._2.plusFifteen).get(id) match {
      case Some(screening) =>

        Ok(views.html.reservescreening(screening,request.session.get("orderid").map(_.toInt).flatMap(orders.get),error))
      case None => Ok("Screening Unavailable")
    }
  }

  def finalizeOrder(screeningId: Int): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
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
                Future(Ok(views.html.screeningFinalization(orderSeats.map{
                  case (screening,(_,Success(list: List[Seat]))) =>
                    screening -> list
                })))
              case list=>
                val string = list.flatMap {
                  case (screening,(_,Failure(list: List[Seat]))) =>
                    s"There was a problem with screening $screening - following seats cannot be left (as long as they're between two reserved seats)"::list.map(_.toString)
                }
                getScreening(screeningId,string).apply(request)
            }
          case None => getScreening(screeningId,List("error")).apply(request)
        }
      case None => getScreening(screeningId,List("error")).apply(request)
    }
  }


  def reserveSeat(screeningId: Int, rowId: Int, seatId: Int ): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    screenings.filter(_._2.plusFifteen).get(screeningId) match {
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
