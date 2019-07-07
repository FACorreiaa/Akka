package com.actors.montecarlo;


import akka.actor.UntypedAbstractActor;

import static com.actors.montecarlo.Main.DARTS_PER_ACTOR;

public class Dart extends UntypedAbstractActor {

  /**
   * Use Monte Carlo integration to approximate pi.
   * //www.wikiwand.com/en/Monte_Carlo_integration)}
   */
  private float approximatePi(long size) {
    int inside = 0; // Keep track of points inside the circle.
    for (int i = 0; i < size; i++) {
      Point p = Point.genRandPoint();
      if (p.x * p.x + p.y * p.y <= 1) { // Check if point is inside circle.
        inside += 1;
      }
    }
    return 4 * ((float) inside) / size;
  }

  @Override
  public void onReceive(Object msg) {
    if (msg != null) {
      getSender().tell(approximatePi(DARTS_PER_ACTOR), getSelf());
    } else {
      getContext().stop(getSelf());
      unhandled(msg);
    }
  }
}