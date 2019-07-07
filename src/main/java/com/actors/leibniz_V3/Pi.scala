import Actors.{Listener, Master}
import Messages.Calculate
import akka.actor.{ActorSystem, Props}

object Pi extends App {
  val system = ActorSystem("PI-akka")

  def calculate(nWorkers: Int, nElements: Int, nMessages: Int): Unit = {
    require(nElements < nMessages)
    require(nElements > 0 && nMessages > 0 && nWorkers > 0)

    val listener = system.actorOf(Props[Listener], "PI-listener")
    val master = system.actorOf(Props(new Master(nWorkers, nMessages, nElements, listener)), "PI-master")

    master ! Calculate
  }

  try {
    val config = system.settings.config
    calculate(10, 10, 10
    )
  } catch {
    case e : Exception => {
      system.log.error("There is no Fault tolerance available for this actor system!")
      system.log.error(e.getMessage.toString)
      system.terminate()
    }
  }
}
