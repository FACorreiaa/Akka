package Messages

import scala.concurrent.duration.Duration

sealed trait PiMessage
final case object Calculate extends PiMessage
final case class Work(start: Int, nElements: Int) extends PiMessage
final case class Result(value: Double) extends PiMessage
final case class PiApproximation(pi:Double, duration: Duration)
