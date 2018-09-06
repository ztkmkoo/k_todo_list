package com.ztkmkoo.todolist.frontend.service;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.pattern.PatternsCS;
import com.ztkmkoo.todolist.common.message.Message;
import com.ztkmkoo.todolist.common.model.ToDo;
import com.ztkmkoo.todolist.frontend.model.ToDoAns;
import com.ztkmkoo.todolist.frontend.model.ToDoReq;
import com.ztkmkoo.todolist.frontend.model.ToDoUpdateAns;
import com.ztkmkoo.todolist.frontend.model.ToDoUpdateReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ToDoService {

    @Autowired
    private ActorSystem actorSystem;

    @Value("${process}")
    private String process;

    public Mono<Message.SelectToDoAns> getToDoListByPage(final int page) {
        log.info("getToDoListByPage: {}", page);
        final ActorSelection actorSelection = actorSystem.actorSelection(process);
        final Message.SelectToDoReq req = new Message.SelectToDoReq(page, 5);

        return Mono.fromCallable(() -> {
            try {
                final CompletableFuture<Object> future = PatternsCS.ask(actorSelection, req, Duration.ofSeconds(3)).toCompletableFuture();
                final Message.SelectToDoAns ans = Message.SelectToDoAns.class.cast(future.get());
                return ans;
            } catch (Exception e) {
                log.error(e.getLocalizedMessage());
                return new Message.SelectToDoAns(page, 5, Collections.EMPTY_LIST, 1);
            }
        });
    }

    public Mono<ToDoAns> insertToDo(final ToDoReq toDoReq) {
        log.info("insert to do req: {}", toDoReq);

        final ActorSelection actorSelection = actorSystem.actorSelection(process);
        final Message.InsertToDoReq req = new Message.InsertToDoReq(toDoReq.getToDo(), toDoReq.getRefList());

        return Mono.fromCallable(() -> {
            final CompletableFuture<Object> future = PatternsCS.ask(actorSelection, req, Duration.ofSeconds(3)).toCompletableFuture();
            final Message.InsertToDoAns ans =  Message.InsertToDoAns.class.cast(future.get());

            return new ToDoAns(new ToDo(
                    ans.getToDo().getId(),
                    ans.getToDo().getToDo(),
                    ans.getToDo().getRefList(),
                    ans.getToDo().getRegisterDate(),
                    ans.getToDo().getModifyDate(),
                    ans.getToDo().getIsDone()), 0);
        });
    }

    public Mono<Message.FinishToDoAns> finishToDo(final int id) {
        final ActorSelection actorSelection = actorSystem.actorSelection(process);
        final Message.FinishToDoReq req = new Message.FinishToDoReq(id);

        return Mono.fromCallable(() -> {
            final CompletableFuture<Object> future = PatternsCS.ask(actorSelection, req, Duration.ofSeconds(3)).toCompletableFuture();
            final Message.FinishToDoAns ans = Message.FinishToDoAns.class.cast(future.get());
            return ans;
        });
    }

    public Mono<ToDoUpdateAns> updateToDo(final ToDoUpdateReq req) {
        final ActorSelection actorSelection = actorSystem.actorSelection(process);
        final Message.UpdateToDoReq updateToDoReq = new Message.UpdateToDoReq(req.getId(), req.getToDo());

        return Mono.fromCallable(() -> {
            final CompletableFuture<Object> future = PatternsCS.ask(actorSelection, updateToDoReq, Duration.ofSeconds(3)).toCompletableFuture();
            final Message.UpdateToDoAns ans = Message.UpdateToDoAns.class.cast(future.get());

            return new ToDoUpdateAns(ans.isDone() ? 1 : 0, ans.getErrorLog());
        });
    }
}
