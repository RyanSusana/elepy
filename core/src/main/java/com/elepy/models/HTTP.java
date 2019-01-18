package com.elepy.models;

import spark.Route;
import spark.*;
import spark.route.HttpMethod;

import java.util.function.Consumer;

/**
 * Wrapper around the Spark Service object.
 *
 * @see <a href="http://javadoc.io/doc/com.sparkjava/spark-core/2.8.0">Spark Documentation</a>
 */
public class HTTP {
    private final spark.Service service;

    public HTTP(Service service) {
        this.service = service;
    }

    public static HTTP ignite() {
        return new HTTP(Service.ignite());
    }

    public HTTP ipAddress(String ipAddress) {
        return new HTTP(service.ipAddress(ipAddress));
    }

    public HTTP port(int port) {
        return new HTTP(service.port(port));
    }

    public int port() {
        return service.port();
    }

    public Service secure(String keystoreFile, String keystorePassword, String truststoreFile, String truststorePassword) {
        return service.secure(keystoreFile, keystorePassword, truststoreFile, truststorePassword);
    }

    public HTTP secure(String keystoreFile, String keystorePassword, String certAlias, String truststoreFile, String truststorePassword) {
        return new HTTP(service.secure(keystoreFile, keystorePassword, certAlias, truststoreFile, truststorePassword));
    }

    public HTTP secure(String keystoreFile, String keystorePassword, String truststoreFile, String truststorePassword, boolean needsClientCert) {
        return new HTTP(service.secure(keystoreFile, keystorePassword, truststoreFile, truststorePassword, needsClientCert));
    }

    public HTTP secure(String keystoreFile, String keystorePassword, String certAlias, String truststoreFile, String truststorePassword, boolean needsClientCert) {
        return new HTTP(service.secure(keystoreFile, keystorePassword, certAlias, truststoreFile, truststorePassword, needsClientCert));
    }

    public HTTP threadPool(int maxThreads) {
        return new HTTP(service.threadPool(maxThreads));
    }

    public HTTP threadPool(int maxThreads, int minThreads, int idleTimeoutMillis) {
        return new HTTP(service.threadPool(maxThreads, minThreads, idleTimeoutMillis));
    }

    public HTTP staticFileLocation(String folder) {
        return new HTTP(service.staticFileLocation(folder));
    }

    public HTTP externalStaticFileLocation(String externalFolder) {
        return new HTTP(service.externalStaticFileLocation(externalFolder));
    }

    public void webSocket(String path, Class<?> handlerClass) {
        service.webSocket(path, handlerClass);
    }

    public void webSocket(String path, Object handler) {
        service.webSocket(path, handler);
    }

    public Service webSocketIdleTimeoutMillis(int timeoutMillis) {
        return service.webSocketIdleTimeoutMillis(timeoutMillis);
    }

    public void notFound(String page) {
        service.notFound(page);
    }

    public void internalServerError(String page) {
        service.internalServerError(page);
    }

    public void notFound(Route route) {
        service.notFound(route);
    }

    public void internalServerError(Route route) {
        service.internalServerError(route);
    }

    public void awaitInitialization() {
        service.awaitInitialization();
    }

    public void stop() {
        service.stop();
    }

    public void awaitStop() {
        service.awaitStop();
    }

    public void path(String path, RouteGroup routeGroup) {
        service.path(path, routeGroup);
    }

    public String getPaths() {
        return service.getPaths();
    }

    public void addRoute(HttpMethod httpMethod, RouteImpl route) {
        service.addRoute(httpMethod, route);
    }

    public void addFilter(HttpMethod httpMethod, FilterImpl filter) {
        service.addFilter(httpMethod, filter);
    }

    @Deprecated
    public void addRoute(String httpMethod, RouteImpl route) {
        service.addRoute(httpMethod, route);
    }

    @Deprecated
    public void addFilter(String httpMethod, FilterImpl filter) {
        service.addFilter(httpMethod, filter);
    }

    public void init() {
        service.init();
    }

    public int activeThreadCount() {
        return service.activeThreadCount();
    }

    public <T extends Exception> void exception(Class<T> exceptionClass, ExceptionHandler<? super T> handler) {
        service.exception(exceptionClass, handler);
    }

    public HaltException halt() {
        return service.halt();
    }

    public HaltException halt(int status) {
        return service.halt(status);
    }

    public HaltException halt(String body) {
        return service.halt(body);
    }

    public HaltException halt(int status, String body) {
        return service.halt(status, body);
    }

    public void initExceptionHandler(Consumer<Exception> initExceptionHandler) {
        service.initExceptionHandler(initExceptionHandler);
    }

    public void get(String path, Route route) {
        service.get(path, route);
    }

    public void post(String path, Route route) {
        service.post(path, route);
    }

    public void put(String path, Route route) {
        service.put(path, route);
    }

    public void patch(String path, Route route) {
        service.patch(path, route);
    }

    public void delete(String path, Route route) {
        service.delete(path, route);
    }

    public void head(String path, Route route) {
        service.head(path, route);
    }

    public void trace(String path, Route route) {
        service.trace(path, route);
    }

    public void connect(String path, Route route) {
        service.connect(path, route);
    }

    public void options(String path, Route route) {
        service.options(path, route);
    }

    public void before(String path, Filter filter) {
        service.before(path, filter);
    }

    public void after(String path, Filter filter) {
        service.after(path, filter);
    }

    public void get(String path, String acceptType, Route route) {
        service.get(path, acceptType, route);
    }

    public void post(String path, String acceptType, Route route) {
        service.post(path, acceptType, route);
    }

    public void put(String path, String acceptType, Route route) {
        service.put(path, acceptType, route);
    }

    public void patch(String path, String acceptType, Route route) {
        service.patch(path, acceptType, route);
    }

    public void delete(String path, String acceptType, Route route) {
        service.delete(path, acceptType, route);
    }

    public void head(String path, String acceptType, Route route) {
        service.head(path, acceptType, route);
    }

    public void trace(String path, String acceptType, Route route) {
        service.trace(path, acceptType, route);
    }

    public void connect(String path, String acceptType, Route route) {
        service.connect(path, acceptType, route);
    }

    public void options(String path, String acceptType, Route route) {
        service.options(path, acceptType, route);
    }

    public void before(Filter filter) {
        service.before(filter);
    }

    public void after(Filter filter) {
        service.after(filter);
    }

    public void before(String path, String acceptType, Filter filter) {
        service.before(path, acceptType, filter);
    }

    public void after(String path, String acceptType, Filter filter) {
        service.after(path, acceptType, filter);
    }

    public void afterAfter(Filter filter) {
        service.afterAfter(filter);
    }

    public void afterAfter(String path, Filter filter) {
        service.afterAfter(path, filter);
    }

    public void get(String path, TemplateViewRoute route, TemplateEngine engine) {
        service.get(path, route, engine);
    }

    public void get(String path, String acceptType, TemplateViewRoute route, TemplateEngine engine) {
        service.get(path, acceptType, route, engine);
    }

    public void post(String path, TemplateViewRoute route, TemplateEngine engine) {
        service.post(path, route, engine);
    }

    public void post(String path, String acceptType, TemplateViewRoute route, TemplateEngine engine) {
        service.post(path, acceptType, route, engine);
    }

    public void put(String path, TemplateViewRoute route, TemplateEngine engine) {
        service.put(path, route, engine);
    }

    public void put(String path, String acceptType, TemplateViewRoute route, TemplateEngine engine) {
        service.put(path, acceptType, route, engine);
    }

    public void delete(String path, TemplateViewRoute route, TemplateEngine engine) {
        service.delete(path, route, engine);
    }

    public void delete(String path, String acceptType, TemplateViewRoute route, TemplateEngine engine) {
        service.delete(path, acceptType, route, engine);
    }

    public void patch(String path, TemplateViewRoute route, TemplateEngine engine) {
        service.patch(path, route, engine);
    }

    public void patch(String path, String acceptType, TemplateViewRoute route, TemplateEngine engine) {
        service.patch(path, acceptType, route, engine);
    }

    public void head(String path, TemplateViewRoute route, TemplateEngine engine) {
        service.head(path, route, engine);
    }

    public void head(String path, String acceptType, TemplateViewRoute route, TemplateEngine engine) {
        service.head(path, acceptType, route, engine);
    }

    public void trace(String path, TemplateViewRoute route, TemplateEngine engine) {
        service.trace(path, route, engine);
    }

    public void trace(String path, String acceptType, TemplateViewRoute route, TemplateEngine engine) {
        service.trace(path, acceptType, route, engine);
    }

    public void connect(String path, TemplateViewRoute route, TemplateEngine engine) {
        service.connect(path, route, engine);
    }

    public void connect(String path, String acceptType, TemplateViewRoute route, TemplateEngine engine) {
        service.connect(path, acceptType, route, engine);
    }

    public void options(String path, TemplateViewRoute route, TemplateEngine engine) {
        service.options(path, route, engine);
    }

    public void options(String path, String acceptType, TemplateViewRoute route, TemplateEngine engine) {
        service.options(path, acceptType, route, engine);
    }

    public void get(String path, Route route, ResponseTransformer transformer) {
        service.get(path, route, transformer);
    }

    public void get(String path, String acceptType, Route route, ResponseTransformer transformer) {
        service.get(path, acceptType, route, transformer);
    }

    public void post(String path, Route route, ResponseTransformer transformer) {
        service.post(path, route, transformer);
    }

    public void post(String path, String acceptType, Route route, ResponseTransformer transformer) {
        service.post(path, acceptType, route, transformer);
    }

    public void put(String path, Route route, ResponseTransformer transformer) {
        service.put(path, route, transformer);
    }

    public void put(String path, String acceptType, Route route, ResponseTransformer transformer) {
        service.put(path, acceptType, route, transformer);
    }

    public void delete(String path, Route route, ResponseTransformer transformer) {
        service.delete(path, route, transformer);
    }

    public void delete(String path, String acceptType, Route route, ResponseTransformer transformer) {
        service.delete(path, acceptType, route, transformer);
    }

    public void head(String path, Route route, ResponseTransformer transformer) {
        service.head(path, route, transformer);
    }

    public void head(String path, String acceptType, Route route, ResponseTransformer transformer) {
        service.head(path, acceptType, route, transformer);
    }

    public void connect(String path, Route route, ResponseTransformer transformer) {
        service.connect(path, route, transformer);
    }

    public void connect(String path, String acceptType, Route route, ResponseTransformer transformer) {
        service.connect(path, acceptType, route, transformer);
    }

    public void trace(String path, Route route, ResponseTransformer transformer) {
        service.trace(path, route, transformer);
    }

    public void trace(String path, String acceptType, Route route, ResponseTransformer transformer) {
        service.trace(path, acceptType, route, transformer);
    }

    public void options(String path, Route route, ResponseTransformer transformer) {
        service.options(path, route, transformer);
    }

    public void options(String path, String acceptType, Route route, ResponseTransformer transformer) {
        service.options(path, acceptType, route, transformer);
    }

    public void patch(String path, Route route, ResponseTransformer transformer) {
        service.patch(path, route, transformer);
    }

    public void patch(String path, String acceptType, Route route, ResponseTransformer transformer) {
        service.patch(path, acceptType, route, transformer);
    }

    public void defaultResponseTransformer(ResponseTransformer transformer) {
        service.defaultResponseTransformer(transformer);
    }
}
