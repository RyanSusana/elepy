package com.elepy.http;

import spark.QueryParamsMap;
import spark.Session;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

public class SparkRequest implements Request {
    private final spark.Request request;

    public SparkRequest(spark.Request request) {
        this.request = request;
    }

    public Map<String, String> params() {
        return request.params();
    }

    @Override
    public String params(String param) {
        return request.params(param);
    }

    public String[] splat() {
        return request.splat();
    }

    @Override
    public String requestMethod() {
        return request.requestMethod();
    }

    @Override
    public String scheme() {
        return request.scheme();
    }

    @Override
    public String host() {
        return request.host();
    }

    public String userAgent() {
        return request.userAgent();
    }

    @Override
    public int port() {
        return request.port();
    }

    public String pathInfo() {
        return request.pathInfo();
    }

    public String servletPath() {
        return request.servletPath();
    }

    public String contextPath() {
        return request.contextPath();
    }

    @Override
    public String url() {
        return request.url();
    }

    public String contentType() {
        return request.contentType();
    }

    @Override
    public String ip() {
        return request.ip();
    }

    @Override
    public String body() {
        return request.body();
    }

    @Override
    public byte[] bodyAsBytes() {
        return request.bodyAsBytes();
    }

    public int contentLength() {
        return request.contentLength();
    }

    @Override
    public String queryParams(String queryParam) {
        return request.queryParams(queryParam);
    }

    @Override
    public String queryParamOrDefault(String queryParam, String defaultValue) {
        return request.queryParamOrDefault(queryParam, defaultValue);
    }

    public String[] queryParamsValues(String queryParam) {
        return request.queryParamsValues(queryParam);
    }

    @Override
    public String headers(String header) {
        return request.headers(header);
    }

    public Set<String> queryParams() {
        return request.queryParams();
    }

    public Set<String> headers() {
        return request.headers();
    }

    public String queryString() {
        return request.queryString();
    }

    public void attribute(String attribute, Object value) {
        request.attribute(attribute, value);
    }

    @Override
    public <T> T attribute(String attribute) {
        return request.attribute(attribute);
    }

    public Set<String> attributes() {
        return request.attributes();
    }

    public HttpServletRequest raw() {
        return request.raw();
    }

    public QueryParamsMap queryMap() {
        return request.queryMap();
    }

    public QueryParamsMap queryMap(String key) {
        return request.queryMap(key);
    }

    public Session session() {
        return request.session();
    }

    public Session session(boolean create) {
        return request.session(create);
    }

    @Override
    public Map<String, String> cookies() {
        return request.cookies();
    }

    @Override
    public String cookie(String name) {
        return request.cookie(name);
    }

    @Override
    public String uri() {
        return request.uri();
    }

    public String protocol() {
        return request.protocol();
    }
}
