package com.ztkmkoo.todolist.frontend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ztkmkoo.todolist.frontend.model.ToDo;
import com.ztkmkoo.todolist.frontend.model.ToDoAns;
import com.ztkmkoo.todolist.frontend.model.ToDoReq;
import com.ztkmkoo.todolist.frontend.service.ToDoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/")
public class ToDoController {

    private final LocalDateTime now = LocalDateTime.now();
    private final List<ToDo> toDoList = new ArrayList<>(Arrays.asList(
            new ToDo(1, "집안일", Collections.emptyList(), now, now, 1),
            new ToDo(2, "청소", new ArrayList<>(Arrays.asList(1)), now, now, 0),
            new ToDo(3, "빨래", new ArrayList<>(Arrays.asList(1, 2)), now, now, 0)
    ));

    @Autowired
    private ToDoService toDoService;

    @GetMapping(path = "/")
    public Rendering index() {
        return Rendering
                .view("index.html")
                .modelAttribute("toDoList", toDoList)
                .build();
    }

    @ResponseBody
    @PostMapping(path = "/insertNewToDo")
    public Mono<ToDoAns> insertNewToDo(@RequestBody final String reqBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ToDoReq toDoReq = mapper.readValue(reqBody, ToDoReq.class);
            return toDoService.insertToDo(toDoReq);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            return Mono.just(new ToDoAns(500));
        }
    }
}
