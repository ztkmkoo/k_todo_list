package com.ztkmkoo.todolist.frontend.handler;

import com.ztkmkoo.todolist.frontend.model.ToDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

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

    public Mono<ServerResponse> test2(ServerRequest request) {
        log.info("test2: {}", request.toString());

        Mono<String> data= request.bodyToMono(String.class);

        Runnable runnable = () -> {
            String bd = data.block();
            System.out.println(bd);
        };
        Thread thread = new Thread(runnable);
        thread.start();

        return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).body(Mono.just("Done"), String.class);
    }
}
