package com.elepy.http;

import com.elepy.Elepy;
import com.elepy.auth.authentication.AuthenticationService;
import com.elepy.auth.authorization.AuthorizationService;
import com.elepy.di.ElepyContext;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.exceptions.ElepyException;
import jakarta.enterprise.context.control.RequestContextController;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HttpServiceInterceptor implements HttpService {


    private static final Logger logger = LoggerFactory.getLogger("HTTP");

    private final ElepyContext elepy;
    private HttpService implementation;

    private boolean started = false;

    private final List<Consumer<HttpService>> actions = new ArrayList<>();

    private int port;

    public HttpServiceInterceptor(Elepy elepy, HttpService implementation) {
        this.elepy = elepy;
        this.implementation = implementation;
        port(1337);
    }

    public boolean hasImplementation() {
        return implementation != null;
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

    public void staticFiles(String path, StaticFileLocation location) {
        //Add staticfiles to the front
        if (started) {
            implementation.staticFiles(path, location);
        } else {
            this.actions.add(0, http -> http.staticFiles(path, location));
        }
    }

    public <T extends Exception> void exception(Class<T> exceptionClass, ExceptionHandler<? super T> exceptionHandler) {
        intercept(http -> http.exception(exceptionClass, (exception, context) -> {
            exceptionHandler.handleException(exception, new DefaultHttpContext(elepy, context));
        }));
    }

    @Override
    public void before(HttpContextHandler contextHandler) {
        intercept(http -> http.before(wrapContextHandler(contextHandler)));
    }

    @Override
    public void after(HttpContextHandler contextHandler) {
        intercept(http -> http.after(wrapContextHandler(contextHandler)));
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
        intercept(HttpService::stop);
    }

    public void port(int port) {
        this.port = port;
        intercept(http -> http.port(port));
    }

    public void addRoute(Route route) {
        logger.debug("Adding route {} {}", route.getMethod(), route.getPath());
        intercept(http -> {
            HttpContextHandler handler = context -> {
                var requestContextController = CDI.current().select(RequestContextController.class).get();
                try{
                    requestContextController.activate();
                    if(!route.getPermissions().isEmpty()){
                        permissionCheck(route, context);
                    }

                    route.getHttpContextHandler().handle(context);
                }finally {
                    requestContextController.deactivate();
                }
            };

            var authenticatedRoute = RouteBuilder.anElepyRoute()
                      .path(route.getPath())
                       .method(route.getMethod())
                    .acceptType(route.getAcceptType())
                    .route(handler).build();

            logRouteMapping(route);
            http.addRoute(authenticatedRoute);
        });
    }

    private static void logRouteMapping(Route route) {
        final int requiredSpace = 7;
        final int paddingToPrepend = requiredSpace - route.getMethod().name().length();
        final String padding = StringUtils.repeat(" ", paddingToPrepend);

        if(route.getPermissions().isEmpty()){
            logger.info("Mapping {}{}\t{}",padding, route.getMethod(), route.getPath());
        }else{
            logger.info("Mapping {}{}\t{} with required permissions:\t{}",padding, route.getMethod(), route.getPath(), route.getPermissions());
        }
    }

    // TODO find a better place to do this
    private void permissionCheck(Route route, HttpContext ctx){

        var cdi = CDI.current();
        var authentication = cdi.select(AuthenticationService.class).get();
        var authorization = cdi.select(AuthorizationService.class).get();

        var credentials = authentication.getCredentials(ctx.request()).orElseThrow(ElepyException::notAuthorized);


        var authorizationResult = authorization.testPermissions(credentials.getPrincipal(), URI.create(route.getPath()), route.getPermissions());

        if(!authorizationResult.isSuccessful()){
            throw ElepyException.notAuthorized();
        }
    }

    private void intercept(Consumer<HttpService> action) {
        if (started) {
            action.accept(implementation);
        } else {
            this.actions.add(action);
        }
    }

    private HttpContextHandler wrapContextHandler(HttpContextHandler ctxHandler) {
        return ctx -> {
            ctxHandler.handle(new DefaultHttpContext(elepy, ctx));

            logger.debug("Handled request");
        };
    }
}
