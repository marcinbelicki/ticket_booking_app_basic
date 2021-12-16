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
  addRoomToMemory(None)
  addRoomToMemory(None)
  val a = new GregorianCalendar()
  a.add(Calendar.YEAR,1)
  rooms(0).addScreening(movies(0),a.getTime)
  a.add(Calendar.MINUTE,5)
  rooms(1).addScreening(movies(0),a.getTime)
  a.add(Calendar.MINUTE,10)
  rooms(2).addScreening(movies(0),a.getTime)
  a.add(Calendar.MINUTE,10)
  rooms(3).addScreening(movies(1),a.getTime)
  a.add(Calendar.HOUR,10)
  rooms(3).addScreening(movies(1),a.getTime)
  rooms(1).addScreening(movies(1),new GregorianCalendar(2021,Calendar.DECEMBER,15,1,0x1).getTime)
  rooms(1).addScreening(movies(2),new GregorianCalendar(2021,Calendar.DECEMBER,15,4,0x1).getTime)
  rooms(0).addScreening(movies(2),new GregorianCalendar(2021,Calendar.DECEMBER,15,4,0).getTime)
  rooms(2).addScreening(movies(0),new GregorianCalendar(2021,Calendar.DECEMBER,13,0,2).getTime)

 lifecycle.addStopHook { () =>
    Future.successful(())
  }
}