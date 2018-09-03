package com.ztkmkoo.todolist.frontend.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
public class TestHandler {

    public Mono<ServerResponse> test(ServerRequest request) {
        log.info("test: {}", request);
        return ServerResponse.ok().contentType(MediaType.TEXT_HTML). render("index");
    }
}
