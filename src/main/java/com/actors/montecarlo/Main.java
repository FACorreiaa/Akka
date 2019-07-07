package com.actors.montecarlo;
import static com.actors.montecarlo.ThrowDarts.*;

public class Main {
    public static int ACTOR_COUNT = 3;

    public static long DARTS_PER_ACTOR = 2000000000;

    // Sum of the Dart Actor results.
    public static double sum = 0.0;
    // Keep track of actors still going.
    public static Integer actorsLeft = ACTOR_COUNT;

    public static void main(String[] args) {
        //long start = System.nanoTime();
        akka.Main.main(new String[]{ThrowDarts.class.getName()});
        //long stop = System.nanoTime();
        //long timeRes = (stop - start)/1_000_000_000;
        //long ttTime = (stop - start) / ACTOR_COUNT;
        //double ttTimeSeconds = ((double) ttTime / 1_000_000_000);
        //System.out.println("Total time: " + timeRes);
        //System.out.println("Time per actor: " + ttTimeSeconds);
    }

}