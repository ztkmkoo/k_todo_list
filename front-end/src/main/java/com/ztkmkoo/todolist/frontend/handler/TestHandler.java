package com.ztkmkoo.todolist.frontend.handler;

import com.ztkmkoo.todolist.frontend.model.ToDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
public class TestHandler {

    public Mono<ServerResponse> test(ServerRequest request) {
        log.info("test: {}", request);

        final List<ToDo> toDoList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        toDoList.add(new ToDo(1, "집안일", Collections.emptyList(), now, now, 1));
        toDoList.add(new ToDo(2, "청소", new ArrayList<>(Arrays.asList(1)), now, now, 0));
        toDoList.add(new ToDo(3, "빨래", new ArrayList<>(Arrays.asList(1, 2)), now, now, 0));

        return ServerResponse.ok().contentType(MediaType.TEXT_HTML). render("index", toDoList);
    }
}
