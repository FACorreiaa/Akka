package Actors

import Messages.{Result, Work}
import akka.actor.Actor
import akka.event.Logging

import scala.annotation.tailrec

class Worker extends Actor  {
  val log = Logging(context.system, this)

  override def preStart(): Unit = {
    log.debug("Worker Starting")
  }

  def calculatePiFor(start: Int, nElements: Int): Double = {
    @tailrec
    def loop(acc: Double, n: Int, end: Int): Double = {
      if (n >= end) acc
      else loop(acc + (1 - (n % 2) * 2) / (2.0 * n + 1), n + 1, end)
    }

    loop(0.0, start, start + nElements)
  }

  override def receive: Actor.Receive = {
    case Work(start, nElems) =>
      log.debug(s"Start Worker with $start and $nElems")
      sender ! Result(calculatePiFor(start, nElems))
  }
}
