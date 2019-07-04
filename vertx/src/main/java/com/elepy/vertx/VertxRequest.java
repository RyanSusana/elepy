package com.elepy.vertx;

import com.elepy.http.Request;
import com.elepy.http.Session;
import com.elepy.uploads.UploadedFile;
import io.vertx.core.http.HttpServerRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class VertxRequest implements Request {
    HttpServerRequest request;


    @Override
    public String params(String param) {
        return request.getParam(param);
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
        //TODO
        return request.remoteAddress().port();
    }

    @Override
    public String url() {
        return request.absoluteURI();
    }

    @Override
    public String ip() {
        //TODO
        return request.remoteAddress().host();
    }

    @Override
    public String body() {
        return null;
    }

    @Override
    public byte[] bodyAsBytes() {
        return new byte[0];
    }

    @Override
    public String queryParams(String queryParam) {
        return null;
    }

    @Override
    public String queryParamOrDefault(String queryParam, String defaultValue) {
        return null;
    }

    @Override
    public String headers(String header) {
        return null;
    }

    @Override
    public <T> T attribute(String attribute) {
        return null;
    }

    @Override
    public Map<String, String> cookies() {
        return null;
    }

    @Override
    public String cookie(String name) {
        return null;
    }

    @Override
    public String uri() {
        return null;
    }

    @Override
    public Session session() {
        return null;
    }

    @Override
    public String pathInfo() {
        return null;
    }

    @Override
    public String servletPath() {
        return null;
    }

    @Override
    public String contextPath() {
        return null;
    }

    @Override
    public Set<String> queryParams() {
        return null;
    }

    @Override
    public Set<String> headers() {
        return null;
    }

    @Override
    public String queryString() {
        return null;
    }

    @Override
    public Map<String, String> params() {
        return null;
    }

    @Override
    public String[] queryParamValues(String key) {
        return new String[0];
    }

    @Override
    public String[] splat() {
        return new String[0];
    }

    @Override
    public List<UploadedFile> uploadedFiles(String key) {
        return null;
    }

    @Override
    public void attribute(String attribute, Object value) {

    }
}
