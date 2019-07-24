package com.elepy.vertx;

import com.elepy.http.Request;
import com.elepy.http.Session;
import com.elepy.uploads.FileUpload;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class VertxRequest implements Request {
    private final HttpServerRequest request;

    private final RoutingContext routingContext;

    public VertxRequest(RoutingContext routingContext) {
        this.routingContext = routingContext;
        this.request = routingContext.request();
    }


    @Override
    public String params(String param) {
        return routingContext.pathParam(param);
    }

    @Override
    public String method() {
        return request.rawMethod();
    }

    @Override
    public String scheme() {
        return request.scheme();
    }

    @Override
    public String host() {
        return request.host();
    }

    @Override
    public int port() {
        return request.remoteAddress().port();
    }

    @Override
    public String url() {
        return request.absoluteURI();
    }

    @Override
    public String ip() {
        return request.remoteAddress().host();
    }

    @Override
    public String body() {
        return routingContext.getBodyAsString();
    }

    @Override
    public byte[] bodyAsBytes() {
        return routingContext.getBody().getBytes();
    }

    @Override
    public String queryParams(String queryParam) {
        return request.getParam(queryParam);
    }

    @Override
    public String queryParamOrDefault(String queryParam, String defaultValue) {
        final String param = queryParams(queryParam);
        return param == null ? defaultValue : param;
    }

    @Override
    public String headers(String header) {
        return request.getHeader(header);
    }

    @Override
    public <T> T attribute(String attribute) {
        return routingContext.get(attribute);
    }

    @Override
    public Map<String, String> cookies() {
        Map<String, String> cookies = new HashMap<>();
        routingContext.cookies().forEach(cookie -> cookies.put(cookie.getName(), cookie.getValue()));
        return cookies;
    }

    @Override
    public String cookie(String name) {
        return routingContext.cookies().stream()
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    @Override
    public String uri() {
        return request.uri();
    }

    @Override
    public Session session() {
        return new VertxSession(routingContext);
    }

    @Override
    public Set<String> queryParams() {
        return request.params().names();
    }

    @Override
    public Set<String> headers() {
        return request.headers().names();
    }

    @Override
    public String queryString() {
        return request.query();
    }

    @Override
    public Map<String, String> params() {
        Map<String, String> toReturn = new HashMap<>();
        request.params().forEach((entry) -> toReturn.put(entry.getKey().toLowerCase(), entry.getValue()));
        return toReturn;
    }

    @Override
    public String[] queryParamValues(String key) {
        return request.params().getAll(key).toArray(new String[0]);
    }

    @Override
    public List<FileUpload> uploadedFiles(String key) {
        return routingContext.fileUploads().stream()
                .filter(file -> file.name().equals(key))
                .map(file -> {
                    try {

                        return FileUpload.of(
                                file.contentType(),
                                new BufferedInputStream(new FileInputStream(new File(file.uploadedFileName()))),
                                file.fileName(),
                                file.size());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void attribute(String attribute, Object value) {
        routingContext.put(attribute, value);
    }
}
