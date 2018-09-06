package com.ztkmkoo.todolist.frontend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ztkmkoo.todolist.common.message.Message;
import com.ztkmkoo.todolist.common.model.ToDo;
import com.ztkmkoo.todolist.frontend.model.ToDoAns;
import com.ztkmkoo.todolist.frontend.model.ToDoReq;
import com.ztkmkoo.todolist.frontend.model.ToDoUpdateAns;
import com.ztkmkoo.todolist.frontend.model.ToDoUpdateReq;
import com.ztkmkoo.todolist.frontend.service.ToDoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/")
public class ToDoController {

    @Autowired
    private ToDoService toDoService;

    @GetMapping(path = "/todo/{page}")
    public Rendering index(@PathVariable int page) {

        final Mono<Message.SelectToDoAns> selectToDoAns = toDoService.getToDoListByPage(page);

        return Rendering
                .view("index.html")
                .modelAttribute("selectToDoAns", selectToDoAns)
                .modelAttribute("currentPage", page)
                .build();
    }

    @ResponseBody
    @PostMapping(path = "/insertNewToDo")
    public Mono<ToDoAns> insertNewToDo(@RequestBody final String reqBody) {
        log.info("/insertNewToDo : {}", reqBody);
        try {
            ObjectMapper mapper = new ObjectMapper();
            ToDoReq toDoReq = mapper.readValue(reqBody, ToDoReq.class);
            return toDoService.insertToDo(toDoReq);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            return Mono.just(new ToDoAns(500));
        }
    }

    @ResponseBody
    @PutMapping(path = "/finishToDo/{id}")
    public Mono<Message.FinishToDoAns> finishToDo(@PathVariable final int id) {
        log.info("/finishToDo : {}", id);
        try {
            return toDoService.finishToDo(id);
        } catch (Exception e) {
            String error = e.getLocalizedMessage();
            log.error(error);
            return Mono.just(new Message.FinishToDoAns(id,0, error));
        }
    }

    @ResponseBody
    @PutMapping(path = "/updateToDo/")
    public Mono<ToDoUpdateAns> updateToDo(@RequestBody final String reqBody) {
        log.info("/finishToDo : {}", reqBody);
        try {
            ObjectMapper mapper = new ObjectMapper();
            ToDoUpdateReq req = mapper.readValue(reqBody, ToDoUpdateReq.class);
            return toDoService.updateToDo(req);
        } catch (Exception e) {
            String error = e.getLocalizedMessage();
            log.error(error);
            return Mono.just(new ToDoUpdateAns(0, error));
        }
    }
}
