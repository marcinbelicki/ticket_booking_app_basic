package controllers

import memory.Memory.{addOrderToMemory, getScreeningsInInterval, groupAndSortByParameter1, orders, screenings}
import memory.Success
import models.Screening
import play.api.mvc._

import java.net.URLDecoder
import java.util.{Calendar, GregorianCalendar}
import javax.inject._


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

  def getScreening(id: Int): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    screenings.filter(_._2.plusFifteen).get(id) match {
      case Some(screening) =>

        Ok(views.html.reservescreening(screening,request.session.get("orderid").map(_.toInt).flatMap(orders.get)))
      case None => Ok("Screening Unavailable")
    }
  }


  def reserveSeat(screeningId: Int, rowId: Int, seatId: Int ): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    screenings.filter(_._2.plusFifteen).get(screeningId) match {
      case Some(screening) =>

        request.session.get("orderid") match {
          case Some(id) =>
            orders.get(id.toInt) match {
              case Some(_) =>
                screening.reserveSeat(rowId,seatId,id.toInt)
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
