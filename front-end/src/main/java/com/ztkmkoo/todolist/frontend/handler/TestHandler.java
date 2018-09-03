package com.ztkmkoo.todolist.frontend.handler;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class TestHandler {

    public Mono<ServerResponse> test(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.TEXT_HTML). render("index");
    }
}
