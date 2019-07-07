package com.actors.leibniz_BETA;

import akka.actor.UntypedAbstractActor;

public class Listener extends UntypedAbstractActor {
    public void onReceive(Object message) {
        if (message instanceof Pi.PiApproximation) {
            Pi.PiApproximation approximation = (Pi.PiApproximation) message;
            System.out.println(String.format("\n\tPi approximation: \t\t%s\n\tCalculation time: \t%s",
                    approximation.getPi(), approximation.getDuration()));
            getContext().system().terminate();
        } else {
            unhandled(message);
        }
    }
}
