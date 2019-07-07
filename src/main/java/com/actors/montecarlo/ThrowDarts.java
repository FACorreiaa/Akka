package com.actors.montecarlo;
import akka.actor.*;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.ActorRef;

import static com.actors.montecarlo.Main.ACTOR_COUNT;
import static com.actors.montecarlo.Main.sum;
import static com.actors.montecarlo.Main.actorsLeft;


/**
 * This class is responsible for creating the "dart-throwing" actors.
 * It is also responsible for averaging the results of the Dart actors.
 */
public class ThrowDarts extends UntypedAbstractActor {

  public static long start = 0;
  public static long stop = 0;
  /**
   * Calculate the "average of the averages".
   * Each Dart actor approximates pi. This function takes those results
   * and averages them to get a *hopefully* better approximation of pi.
   * TODO: There is almost certainly a better reduction method.
   */
  public void onReceive(Object msg) {
    if (msg != null) {
      sum += (Float) msg;
      actorsLeft--;
      if (actorsLeft == 0) {
        System.out.println(sum / ACTOR_COUNT);
        getContext().stop(getSelf());
      }
    } else {
      unhandled(msg);
    }
  }

  /**
   * Initializes a number of darts and tells the Dart actors
   * and tells them to start computing.
   */
  @Override
  public void preStart() {
    start = System.nanoTime();

    for (int i = 0; i < ACTOR_COUNT; i++) {
      final ActorRef dart = getContext()
              .actorOf(
                      Props.create(Dart.class),
                      "dart" + Integer.toString(i));
      // The choice of "0" is used, but anything non-null would
      // work. (If it were null, the Dart actor would die before
      // it did any work.
      dart.tell(0, getSelf());
    }
    stop = System.nanoTime();

  }
}