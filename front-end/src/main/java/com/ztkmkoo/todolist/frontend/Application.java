package com.ztkmkoo.todolist.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

//    @PostConstruct
//    public void testAkka() {
//        final ActorSystem actorSystem = ActorSystem.create();
//
//        ActorSelection actorSelection = actorSystem.actorSelection("akka.tcp://ActorSystem@127.0.0.1:12553/user/test");
//        actorSelection.tell("Hi", ActorRef.noSender());
//    }
}
