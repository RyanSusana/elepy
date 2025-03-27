package com.elepy.http;

import com.elepy.auth.authentication.AuthenticationService;
import com.elepy.auth.authentication.Credentials;
import com.elepy.auth.authorization.AuthorizationResult;
import com.elepy.auth.authorization.AuthorizationService;
import com.elepy.di.ElepyContext;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class HttpServiceConfiguration implements HttpService {

    private final ElepyContext elepy;
    private HttpService implementation;

    private boolean started = false;

    private final List<Consumer<HttpService>> actions = new ArrayList<>();

    private int port;

    public HttpServiceConfiguration(ElepyContext elepy) {
        this(elepy, null);
    }

    public HttpServiceConfiguration(ElepyContext elepy, HttpService implementation) {
        this.implementation = implementation;
        this.elepy = elepy;
        port(1337);
    }

    public boolean hasImplementation() {
        return implementation != null;
    }

    private void add(Consumer<HttpService> action) {
        if (started) {
            action.accept(implementation);
        } else {
            this.actions.add(action);
        }
    }

    public void setImplementation(HttpService implementation) {
        this.implementation = implementation;
    }

    public int port() {
        if (!started) {
            return this.port;
        } else {
            return implementation.port();
        }
    }

    public void ignite() {
        if (implementation == null) {
            throw new ElepyConfigException("Please provide an implementation of HttpService to Elepy");
        }
        actions.forEach(httpServiceConsumer -> httpServiceConsumer.accept(implementation));
        implementation.ignite();
        started = true;
    }

    public void stop() {
        add(HttpService::stop);
    }

    public void port(int port) {
        this.port = port;
        add(http -> http.port(port));
    }

    public void addRoute(Route route) {
        add(http -> {
            HttpContextHandler handler = context -> {

                if(!route.getPermissions().isEmpty()){
                   permissionCheck(route, context);
                }

                route.getHttpContextHandler().handle(context);

                // TODO End request scope
            };

            var authenticatedRoute = RouteBuilder.anElepyRoute()
                      .path(route.getPath())
                       .method(route.getMethod())
                    .acceptType(route.getAcceptType())
                    .route(handler).build();
            http.addRoute(authenticatedRoute);
        });
    }

    // TODO find a better place to do this
    private void permissionCheck(Route route, HttpContext ctx){
        var authentication = ctx.elepy().getDependency(AuthenticationService.class);
        var authorization = ctx.elepy().getDependency(AuthorizationService.class);

        var credentials = authentication.getCredentials(ctx.request()).orElseThrow(ElepyException::notAuthorized);


        var authorizationResult = authorization.testPermissions(credentials.getPrincipal(), URI.create(route.getPath()), route.getPermissions());

        if(!authorizationResult.isSuccessful()){
            throw ElepyException.notAuthorized();
        }
    }

    public void staticFiles(String path, StaticFileLocation location) {
        //Add staticfiles to the front
        if (started) {
            implementation.staticFiles(path, location);
        } else {
            this.actions.add(0, http -> http.staticFiles(path, location));
        }
    }

    public <T extends Exception> void exception(Class<T> exceptionClass, ExceptionHandler<? super T> exceptionHandler) {
        add(http -> http.exception(exceptionClass, (exception, context) -> exceptionHandler.handleException(exception, new DefaultHttpContext(elepy, context))));
    }

    @Override
    public void before(HttpContextHandler contextHandler) {
        add(http -> http.before(wrapContextHandler(contextHandler)));
    }

    @Override
    public void after(HttpContextHandler contextHandler) {
        add(http -> http.after(wrapContextHandler(contextHandler)));
    }

    public HttpContextHandler wrapContextHandler(HttpContextHandler ctxHandler) {
        return ctx -> ctxHandler.handle(new DefaultHttpContext(elepy, ctx));
    }
}
