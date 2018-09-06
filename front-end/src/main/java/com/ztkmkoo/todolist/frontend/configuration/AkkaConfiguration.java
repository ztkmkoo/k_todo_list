package com.ztkmkoo.todolist.frontend.configuration;

import akka.actor.ActorSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AkkaConfiguration {

    @Bean
    public ActorSystem actorSystem() {
        return ActorSystem.create();
    }
}
