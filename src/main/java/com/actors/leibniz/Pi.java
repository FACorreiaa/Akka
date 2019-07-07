package com.actors.leibniz;

import akka.actor.*;

import java.time.Duration;

public class Pi {

    static int messages = 10_000;
    static int elements = 1_000_000;

    public static void main(String[] args) {
        Pi pi = new Pi();
        pi.calculate(4, messages, elements);
    }

    // actors and messages ...

    public void calculate(final int nrOfWorkers, final int nrOfMessages, final int nrOfElements) {
        // Create an Akka system
        ActorSystem system = ActorSystem.create("PiSystem");

        // create the result listener, which will print the result and shutdown the system
        //final ActorRef listener = system.actorOf(new Props(Listener.class), "listener");
        ActorRef listener = system.actorOf(Props.create(Listener.class), "listener");

        ActorRef master = system.actorOf(Props.create(Master.class, nrOfWorkers, nrOfMessages, nrOfElements, listener), "master");

        /*ActorRef master = system.actorOf(new Props(new UntypedActorFactory() {
            public UntypedAbstractActor create() {
                return new Master(nrOfWorkers, nrOfMessages, nrOfElements, listener);
            }
        }), "master");*/

        // start the calculation
        master.tell(new Calculate(), ActorRef.noSender());
    }


    static class Calculate {
    }

    static class Work {
        private final int start;
        private final int nrOfElements;

        public Work(int start, int nrOfElements) {
            this.start = start;
            this.nrOfElements = nrOfElements;
        }

        public int getStart() {
            return start;
        }

        public int getNrOfElements() {
            return nrOfElements;
        }
    }

    static class Result {
        private final double value;

        public Result(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }
    }

    static class PiApproximation {
        private final double pi;
        private final long duration;

        public PiApproximation(double pi, long duration) {
            this.pi = pi;
            this.duration = duration;
        }

        public double getPi() {
            return pi;
        }

        public long getDuration() {
            return duration;
        }
    }

}
