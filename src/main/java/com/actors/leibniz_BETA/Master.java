package com.actors.leibniz_BETA;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.routing.RoundRobinPool;

public class Master extends UntypedAbstractActor {

    public final int nrOfMessages;
    public final int nrOfElements;

    public double pi = 0;
    public int nrOfResults;
    public final long start = System.currentTimeMillis();

    public final ActorRef listener;
    public final ActorRef workerRouter;

    public Master(final int nrOfWorkers, int nrOfMessages, int nrOfElements, ActorRef listener) {
        this.nrOfMessages = nrOfMessages;
        this.nrOfElements = nrOfElements;
        this.listener = listener;

        /*workerRouter = this.getContext().actorOf(Props.create(Worker.class).withRouter(new RoundRobinRouter(nrOfWorkers)),
                "workerRouter");*/
        workerRouter = this.getContext().actorOf(new RoundRobinPool(nrOfWorkers).props(Props.create(Worker.class)), "workerRouter");
    }

    public void onReceive(Object message) {
        if (message instanceof Pi.Calculate) {
            for (int start = 0; start < nrOfMessages; start++) {
                workerRouter.tell(new Pi.Work(start, nrOfElements), getSelf());
            }
        } else if (message instanceof Pi.Result) {
            Pi.Result result = (Pi.Result) message;
            pi += result.getValue();
            nrOfResults += 1;
            if (nrOfResults == nrOfMessages) {
                // Send the result to the listener
                long duration = ((System.currentTimeMillis() - start)/1_000_000_000);
                listener.tell(new Pi.PiApproximation(pi, duration), getSelf());
                // Stops this actor and all its supervised children
                getContext().stop(getSelf());
            }
        } else {
            unhandled(message);
        }
    }
}
