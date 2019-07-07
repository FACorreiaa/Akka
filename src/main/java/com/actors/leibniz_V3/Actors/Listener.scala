package Actors
import Messages.PiApproximation
import akka.actor.Actor
import akka.event.Logging

class Listener extends Actor {
  val log = Logging(context.system, this)
  override def receive = {
    case PiApproximation(computedPi, duration) =>
      val errorPI = Math.abs(Math.PI - computedPi)

      log.info("computed PI = %.15f (%d ms)".format(computedPi, duration.toMillis))
      log.info("PI          = %.15f".format(Math.PI))
      log.info("Error       = %e --- %3.6f%%".format(errorPI, 100.0 / Math.PI * errorPI))
      context.system.terminate()
  }
}
