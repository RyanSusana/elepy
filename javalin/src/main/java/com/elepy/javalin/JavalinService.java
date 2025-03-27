package com.elepy.javalin;

import com.elepy.http.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HandlerType;
import io.javalin.http.staticfiles.Location;
import org.jetbrains.annotations.NotNull;

public class JavalinService implements HttpService {

    private int port;
    private final Javalin javalin;

    public JavalinService() {
        this.javalin = Javalin.create();
        this.port = 1337;
    }

    @Override
    public void port(int port) {
        this.port = port;

    }

    @Override
    public int port() {
        return this.port;
    }

    @Override
    public void addRoute(Route route) {
        javalin.addHandler(HandlerType.valueOf(route.getMethod().name()),
                route.getPath().replaceAll(":([a-zA-Z0-9-_~!$&'()*+,;=:@.]+)", "{$1}"), context -> route.getHttpContextHandler().handle(new JavalinContext(context)));
    }

    @Override
    public void ignite() {
        javalin._conf.showJavalinBanner = false;
        javalin.start(port);
    }

    @Override
    public void stop() {
        javalin.stop();
    }

    @Override
    public void staticFiles(String path, StaticFileLocation location) {
        javalin._conf.addStaticFiles(path, Location.valueOf(location.name()));
    }

    @Override
    public <T extends Exception> void exception(Class<T> exceptionClass, ExceptionHandler<? super T> handler) {
        javalin.exception(exceptionClass, (t, context) -> handler.handleException(t, new JavalinContext(context)));
    }

    @Override
    public void after(HttpContextHandler requestResponseHandler) {
        javalin.after(ctx -> requestResponseHandler.handle(new JavalinContext(ctx)));
    }

    @Override
    public void before(HttpContextHandler contextHandler) {
        javalin.before(ctx -> contextHandler.handle(new JavalinContext(ctx)));
    }
}
