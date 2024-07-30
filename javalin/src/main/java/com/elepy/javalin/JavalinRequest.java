package com.elepy.javalin;


import com.elepy.http.Request;
import com.elepy.http.Session;
import com.elepy.uploads.RawFile;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;

import java.util.*;
import java.util.stream.Collectors;

public class JavalinRequest implements Request {

    private final Context context;

    public JavalinRequest(Context context) {
        this.context = context;
    }

    @Override
    public String params(String param) {
        return context.pathParam(param);
    }

    @Override
    public String method() {
        return context.method();
    }

    @Override
    public String scheme() {
        return context.scheme();
    }

    @Override
    public String host() {
        return context.host();
    }

    @Override
    public int port() {
        return context.port();
    }

    @Override
    public String url() {
        return context.url();
    }

    @Override
    public String ip() {
        return context.ip();
    }

    @Override
    public String body() {
        return context.body();
    }

    @Override
    public byte[] bodyAsBytes() {
        return context.bodyAsBytes();
    }

    @Override
    public String queryParams(String queryParam) {
        return context.queryParam(queryParam);
    }

    @Override
    public String queryParamOrDefault(String queryParam, String defaultValue) {
        return Optional.ofNullable(context.queryParam(queryParam)).orElse(defaultValue);
    }

    @Override
    public String headers(String header) {
        return context.header(header);
    }

    @Override
    public Map<String, String> cookies() {
        return context.cookieMap();
    }

    @Override
    public String cookie(String name) {
        return context.cookie(name);
    }

    @Override
    public String uri() {
        return context.path();
    }

    @Override
    public Session session() {
        return new JavalinSession(context);
    }

    @Override
    public Set<String> queryParams() {
        return context.queryParamMap().keySet();
    }

    @Override
    public Set<String> headers() {
        return context.headerMap().keySet();
    }

    @Override
    public String queryString() {
        return context.queryString();
    }

    @Override
    public Map<String, String> params() {
        return context.pathParamMap();
    }

    @Override
    public String[] queryParamValues(String key) {
        return context.queryParams(key).toArray(String[]::new);
    }

    @Override
    public <T> T attribute(String attribute) {
        return context.attribute(attribute);
    }

    @Override
    public void attribute(String attribute, Object value) {
        context.attribute(attribute, value);
    }

    @Override
    public Set<String> attributes() {
        return context.attributeMap().keySet();
    }

    @Override
    public List<RawFile> uploadedFiles(String key) {
        return context.uploadedFiles(key).stream().map(this::toElepyFile).collect(Collectors.toList());
    }

    @Override
    public RawFile uploadedFile(String key) {
        return toElepyFile(Objects.requireNonNull(context.uploadedFile(key)));
    }

    private RawFile toElepyFile(UploadedFile uploadedFile) {
        return RawFile.of(uploadedFile.getFilename(), uploadedFile.getContentType(), uploadedFile.getContent(), uploadedFile.getSize());
    }


}
