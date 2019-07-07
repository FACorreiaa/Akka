package Actors

import Messages.{Calculate, PiApproximation, Result, Work}
import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorRef, AllForOneStrategy, Props, SupervisorStrategy}
import akka.event.Logging
import akka.japi.pf.DeciderBuilder
import akka.routing.FromConfig

import scala.language.postfixOps
import scala.concurrent.duration._

class Master(nWorkers: Int, nMesagges: Int, nElements: Int, listener: ActorRef) extends Actor {
  var computedPi: Double = _
  var nResults: Int = _
  val startTime: Long = System.currentTimeMillis
  val workerRouter: ActorRef = context.actorOf(FromConfig.props(Props[Worker]), "PI-router")
  val log = Logging(context.system, this)

  override def receive = {
    case Calculate => {
      for (i <- 0 until nMesagges) workerRouter ! Work(i * nElements, nElements)
    }
    case Result(value) => {
      computedPi += value
      nResults += 1
      log.debug(s"[$nResults] computedPi = $computedPi")
      if (nResults == nMesagges) {
        listener ! PiApproximation(4.0 * computedPi, (System.currentTimeMillis - startTime).millis)
        context.stop(self)
      }
    }
  }

  override def supervisorStrategy: SupervisorStrategy = SupervisorStrategy.stoppingStrategy
}
