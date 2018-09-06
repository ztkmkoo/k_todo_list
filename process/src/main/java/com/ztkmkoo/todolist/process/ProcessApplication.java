package com.ztkmkoo.todolist.process;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.ztkmkoo.todolist.process.actor.MasterActor;
import com.ztkmkoo.todolist.process.actor.message.ProcessMessage;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;

public class ProcessApplication {
    public static void main(String[] args) throws Exception {

        final Ignite ignite = Ignition.start();

        final ActorSystem actorSystem = ActorSystem.create("ActorSystem");
        ActorRef actorRef = actorSystem.actorOf(MasterActor.props(ignite), "master");
        actorRef.tell(new ProcessMessage.StartMasterActor(true), ActorRef.noSender());

        while (true) {
            Thread.sleep(1000);
        }
    }
}
