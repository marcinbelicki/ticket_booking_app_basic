package start

import memory.Memory.{addMovieToMemory, addRoomToMemory, movies, rooms, screenings}
import play.api.inject.ApplicationLifecycle

import java.util.{Calendar, GregorianCalendar}
import javax.inject._
import scala.concurrent.Future

// This creates an `start.ApplicationStart` object once at start-up and registers hook for shut-down.
@Singleton
class ApplicationStart @Inject() (lifecycle: ApplicationLifecycle) {


  addMovieToMemory("Joker",120)
  addMovieToMemory("Friday",120)
  addMovieToMemory("Fight Club",125)

  addRoomToMemory(None)
  addRoomToMemory(None)

  rooms(0).addScreening(movies(0),new GregorianCalendar(2021,Calendar.DECEMBER,12).getTime)
  rooms(0).addScreening(movies(0),new GregorianCalendar(2021,Calendar.DECEMBER,13).getTime)
  rooms(1).addScreening(movies(1),new GregorianCalendar(2021,Calendar.DECEMBER,15,1,0x1).getTime)
  rooms(1).addScreening(movies(2),new GregorianCalendar(2021,Calendar.DECEMBER,15,4,0x1).getTime)


  println(movies)
  println("AAAAAA")
 lifecycle.addStopHook { () =>
    Future.successful(())
  }
}