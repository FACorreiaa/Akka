
package com.actors.montecarlo;
import java.util.concurrent.ThreadLocalRandom;

public class Point {
  public double x;
  public double y;

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Generates a random point in the unit square.
   * @return a point in the unit square
   */
  static public Point genRandPoint() {
    return new Point(
            ThreadLocalRandom
                    .current()
                    .nextDouble(-1, 1),
            ThreadLocalRandom
                    .current()
                    .nextDouble(-1, 1)
    );
  }
}