package com.ztkmkoo.todolist.frontend;

import com.ztkmkoo.todolist.frontend.handler.TestHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.reactive.result.view.ViewResolver;
import reactor.ipc.netty.http.server.HttpServer;

@Slf4j
@Component
public class Server {

    private final int PORT = 8187;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ViewResolver viewResolver;

    public void startReactorServer() {
        log.info("Try to start  reactor server..");

        HandlerStrategies.Builder builder = HandlerStrategies.builder();
        builder.viewResolver(viewResolver);

        final HttpHandler httpHandler = RouterFunctions.toHttpHandler(routerFunction(), builder.build());
        final ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
        final HttpServer httpServer = HttpServer.create(PORT);
        httpServer.newHandler(adapter).block();

        log.info("Hi");
    }

    public RouterFunction<ServerResponse> routerFunction() {
        TestHandler testHandler = new TestHandler();

        return RouterFunctions.nest(
                RequestPredicates.path("/"),
                RouterFunctions.nest(
                        RequestPredicates.accept(MediaType.APPLICATION_JSON),
                        RouterFunctions.route(RequestPredicates.method(HttpMethod.GET), testHandler::test))
                );
    }
}
