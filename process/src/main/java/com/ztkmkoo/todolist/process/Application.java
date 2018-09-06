package com.ztkmkoo.todolist.process;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.ztkmkoo.todolist.common.message.Message;

import java.time.Duration;
import java.time.LocalDateTime;

public class Application {
    public static void main(String[] args) throws InterruptedException {
        final ActorSystem actorSystem = ActorSystem.create("ActorSystem");

        ActorRef actorRef = actorSystem.actorOf(Props.create(TestActor.class), "test");
        System.out.println(actorRef.path());

        while (true) {
            Thread.sleep(1000);
        }
    }

    static class TestActor extends AbstractActor {

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(Message.InsertToDoReq.class, msg -> {
                        LocalDateTime now = LocalDateTime.now();
                        Message.InsertToDoAns ans = new Message.InsertToDoAns(3, msg.getToDo(), msg.getRefList(), now, now, 0);
                    })
                    .matchAny(o -> {
                        System.out.println(o.toString());
                        getContext().getSystem().scheduler().scheduleOnce(Duration.ofSeconds(3), sender(), o, getContext().dispatcher(), self());
                    })
                    .build();
        }
    }
}
