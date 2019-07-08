package com.actors.montecarlo_beta;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.routing.RoundRobinPool;
import com.typesafe.config.ConfigFactory;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class Pi {

    static volatile CountDownLatch latch;
    static long timSum = 0;
    public static double x = 0;
    public static double y = 0;
    public static int nSuccess = 0;

    public static void main(String[] args) throws InterruptedException {
        Pi pi = new Pi();
        //int numStepsPerComp = 1000;
        //int numJobs = 100000;
        Scanner scannerObj = new Scanner(System.in);

        System.out.print("Informe a quantidade de iterações a calcular: ");
        int numeroPontos = scannerObj.nextInt();
        System.out.println("Nº Iterações : " + numeroPontos);

        System.out.print("Informe a quantidade de workers: ");
        int numWorkers = scannerObj.nextInt();
        System.out.println("Nº workers : " + numWorkers);

        System.out.print("Informe a quantidade de ciclos: ");
        int nrOfMessages = scannerObj.nextInt();
        System.out.println("Nº Mensagens : " + nrOfMessages);


        //final int MAX_ACT = 16;
        String results[] = new String[numWorkers];

        for (int numActors = 1; numActors <= numWorkers; numActors++) {
            timSum = 0;
            for (int i = 0; i < 30; i++) {
                latch = new CountDownLatch(1);
                pi.calculate(numWorkers, nrOfMessages, numeroPontos);
                latch.await();
                if ( i == 20 ) { // take last 10 samples only
                    timSum = 0;
                }
            }
            results[numActors-1] = "average "+numActors+" threads : "+(timSum/10/1000/1000);
        }

        for (int i = 0; i < results.length; i++) {
            String result = results[i];
            System.out.println(result);
        }
    }

    static class Calculate {
    }

    static class Work {
        private final int numeroPontos;

        /**
         * @param numeroPontos
         */
        public Work(int numeroPontos) {
            this.numeroPontos = numeroPontos;
        }

        public int getNumeroPontos() {
            return numeroPontos;
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

    public static class Worker extends UntypedAbstractActor {

        private double calculatePiFor(long numeroPontos) {
            System.out.println(numeroPontos);
            for (long i = 1; i <= numeroPontos; i++) {
                x = Math.random();
                y = Math.random();
                if (x * x + y * y <= 1)
                    nSuccess++;
            }
            System.out.println(nSuccess);
            return (4.0 * nSuccess / numeroPontos);
        }

        public void onReceive(Object message) {
            if (message instanceof Work) {
                Work work = (Work) message;
                double result = calculatePiFor(work.getNumeroPontos());
                getSender().tell(new Result(result), getSelf());
            } else {
                unhandled(message);
            }
        }
    }

    public static class Master extends UntypedAbstractActor {
        private int numeroPontos;
        private int nrOfMessages;
        private int nrOfWorkers;
        private double pi;
        private int nrOfResults;
        private final long start = System.nanoTime();

        private final ActorRef listener;
        private final ActorRef workerRouter;

        public Master(
                final int nrOfWorkers,
                int nrOfMessages,
                int numeroPontos,
                ActorRef listener) {

            this.nrOfWorkers = nrOfWorkers;
            this.nrOfMessages = nrOfMessages;
            this.numeroPontos = numeroPontos;
            this.listener = listener;

            workerRouter = this.getContext().actorOf(new RoundRobinPool(nrOfWorkers).props(Props.create(Worker.class)), "workerRouter");

        }

        public void onReceive(Object message) {
            if (message instanceof Calculate) {
                for (int i = 0; i < numeroPontos; i++) {
                    workerRouter.tell(new Work(numeroPontos), getSelf());
                }
            } else if (message instanceof Result) {
                Result result = (Result) message;
                pi += result.getValue();
                nrOfResults += 1;
                if (nrOfResults == numeroPontos) {
                    // Send the result to the listener
                    long duration = System.nanoTime() - start;
                    listener.tell(new PiApproximation(pi, duration), getSelf());
                    // Stops this actor and all its supervised children
                    getContext().stop(getSelf());
                }
            } else {
                unhandled(message);
            }
        }
    }

    public static class Listener extends UntypedAbstractActor {
        public void onReceive(Object message) {
            if (message instanceof PiApproximation) {
                PiApproximation approximation = (PiApproximation) message;
                long duration = approximation.getDuration();
                System.out.println(String.format("Pi approximation: " +
                                "%s Calculation time: %s",
                        approximation.getPi(), (double)duration/1_000_000_000));
                timSum += duration;
                getContext().system().terminate();
                latch.countDown();
            } else {
                unhandled(message);
            }
        }
    }

    public void calculate(
            final int nrOfWorkers,
            final int nrOfMessages,
            final int numeroPontos) {


        // Create an Akka system
        ActorSystem system = ActorSystem.create("PiSystem", ConfigFactory.parseString(
                "akka {\n" +
                        "  actor.default-dispatcher {\n" +
                        "      fork-join-executor {\n" +
                        "        parallelism-min = 2\n" +
                        "        parallelism-factor = 0.4\n" +
                        "        parallelism-max = "+nrOfWorkers+"\n" +
                        "      }\n" +
                        "      throughput = 1000\n" +
                        "  }\n" +
                        "\n" +
                        "  log-dead-letters = off\n" +
                        "\n" +
                        "  actor.default-mailbox {\n" +
                        "    mailbox-type = \"akka.dispatch.SingleConsumerOnlyUnboundedMailbox\"\n" +
                        "  }\n" +
                        "}"
                )
        );

        // create the result listener, which will print the result and shutdown the system
        //final ActorRef listener = system.actorOf(new Props(Listener.class), "listener");
        // create the result listener, which will print the result and shutdown the system
        //final ActorRef listener = system.actorOf(new Props(Listener.class), "listener");
        ActorRef listener = system.actorOf(Props.create(Listener.class), "listener");

        // create the master
        ActorRef master = system.actorOf(Props.create(Master.class, nrOfWorkers, nrOfMessages, numeroPontos, listener), "master");


        // start the calculation
        master.tell(new Calculate(), master);
    }
}
