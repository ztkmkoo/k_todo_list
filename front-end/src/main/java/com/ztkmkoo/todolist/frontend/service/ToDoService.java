package com.ztkmkoo.todolist.frontend.service;

import akka.actor.ActorSystem;
import com.ztkmkoo.todolist.frontend.model.ToDoAns;
import com.ztkmkoo.todolist.frontend.model.ToDoReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ToDoService {

    @Autowired
    private ActorSystem actorSystem;

    public Mono<ToDoAns> insertToDo(final ToDoReq toDoReq) {
        return Mono.just(new ToDoAns());
    }
}
