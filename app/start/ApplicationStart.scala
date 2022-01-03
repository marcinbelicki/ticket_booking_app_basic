package start

import memory.Memory.{addMovieToMemory, addRoomToMemory, movies, rooms}
import play.api.inject.ApplicationLifecycle

import java.util.{Calendar, GregorianCalendar}
import javax.inject._
import scala.concurrent.Future

// This creates an `start.ApplicationStart` object once at start-up and registers hook for shut-down.
@Singleton
class ApplicationStart @Inject()(lifecycle: ApplicationLifecycle) {


  addMovieToMemory("Joker", 122) // title: Joker, duration: 122 minutes
  addMovieToMemory("Friday the 13th", 95) // title: Friday the 13th, duration: 95 minutes
  addMovieToMemory("Fight Club", 149) // title: Fight Club, duration: 149minutes

  addRoomToMemory(None)
  addRoomToMemory(None)
  addRoomToMemory(None)
  val calendar = new GregorianCalendar()
  calendar.add(Calendar.MINUTE, 20)

  rooms(0).addScreening(movies(0), calendar.getTime)
  rooms(1).addScreening(movies(1), calendar.getTime)
  rooms(2).addScreening(movies(2), calendar.getTime)
  calendar.add(Calendar.MINUTE, 151)

  rooms(0).addScreening(movies(2), calendar.getTime)
  rooms(1).addScreening(movies(0), calendar.getTime)
  rooms(2).addScreening(movies(1), calendar.getTime)
  lifecycle.addStopHook { () =>
    Future.successful(())
  }
}