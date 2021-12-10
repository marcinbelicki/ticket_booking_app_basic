package controllers

import memory.Memory.{getScreeningsInInterval, groupAndSortByParameter, screenings}
import models.Screening

import javax.inject._
import play.api._
import play.api.mvc._

import java.io.File
import java.net.URLDecoder
import java.nio.charset.{Charset, StandardCharsets}
import java.util.{Calendar, GregorianCalendar}


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

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok("views.html.index()")

  }

  def timeInterval(year1: Int,month1: Int,day1: Int,hour1: Int,minutes1: Int,year2: Int,month2: Int,day2: Int,hour2: Int,minutes2: Int) =
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
      val b = groupAndSortByParameter(screeningsInInterval,List(k,f,g,h,i,j),"")




      Ok(b.mkString("\n"))

    }

  def getString(string: String) = Action { implicit request: Request[AnyContent] =>
    val converted = URLDecoder.decode(string,"UTF-8")


    converted.foreach(println)

    Ok(converted)

  }


}
