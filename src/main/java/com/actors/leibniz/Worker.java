package com.actors.leibniz;

import akka.actor.UntypedActor;

public class Worker extends UntypedActor {

    /*static BigDecimal ONE = new BigDecimal(1);
    static BigDecimal TWO = new BigDecimal(2);
    static BigDecimal FOUR = TWO.pow(2);*/

    public double calculatePiFor(int start, int nrOfElements) {
        double acc = 0;
        for (int i = start * nrOfElements; i <= ((start + 1) * nrOfElements - 1); i++) {
            acc += calculatePiFor(i);
        }
        return acc;
    }

    public static double calculatePiFor(int elem) {
        return (4.0 * (1 - (elem % 2) * 2)) / (2 * elem + 1);
    }


    public void onReceive(Object message) {
        if (message instanceof Pi.Work) {
            Pi.Work work = (Pi.Work) message;
            double result = calculatePiFor(work.getStart(), work.getNrOfElements());
            getSender().tell(new Pi.Result(result), getSelf());
        } else {
            unhandled(message);
        }
    }
}
