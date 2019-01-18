package com.elepy.models;

import spark.QueryParamsMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Wrapper around the Spark Request object. With extra Elepy functionality.
 *
 * @see <a href="http://javadoc.io/doc/com.sparkjava/spark-core/2.8.0">Spark Documentation</a>
 */
public class Request {

    private final spark.Request sparkRequest;

    public Request(spark.Request request) {
        this.sparkRequest = request;
    }

    public Optional<String> modelId() {
        return Optional.ofNullable(params("id"));
    }

    public Map<String, String> params() {
        return sparkRequest.params();
    }

    public String params(String param) {
        return sparkRequest.params(param);
    }

    public String requestMethod() {
        return sparkRequest.requestMethod();
    }

    public String[] splat() {
        return sparkRequest.splat();
    }

    public String scheme() {
        return sparkRequest.scheme();
    }

    public String host() {
        return sparkRequest.host();
    }

    public String userAgent() {
        return sparkRequest.userAgent();
    }

    public int port() {
        return sparkRequest.port();
    }

    public String pathInfo() {
        return sparkRequest.pathInfo();
    }

    public String servletPath() {
        return sparkRequest.servletPath();
    }

    public String contextPath() {
        return sparkRequest.contextPath();
    }

    public String url() {
        return sparkRequest.url();
    }

    public String contentType() {
        return sparkRequest.contentType();
    }

    public String ip() {
        return sparkRequest.ip();
    }

    public String body() {
        return sparkRequest.body();
    }

    public byte[] bodyAsBytes() {
        return sparkRequest.bodyAsBytes();
    }

    public int contentLength() {
        return sparkRequest.contentLength();
    }

    public String queryParams(String queryParam) {
        return sparkRequest.queryParams(queryParam);
    }

    public String queryParamOrDefault(String queryParam, String defaultValue) {
        return sparkRequest.queryParamOrDefault(queryParam, defaultValue);
    }

    public String[] queryParamsValues(String queryParam) {
        return sparkRequest.queryParamsValues(queryParam);
    }

    public String headers(String header) {
        return sparkRequest.headers(header);
    }

    public Set<String> queryParams() {
        return sparkRequest.queryParams();
    }

    public Set<String> headers() {
        return sparkRequest.headers();
    }

    public String queryString() {
        return sparkRequest.queryString();
    }

    public void attribute(String attribute, Object value) {
        sparkRequest.attribute(attribute, value);
    }

    public <T> T attribute(String attribute) {
        return sparkRequest.attribute(attribute);
    }

    public Set<String> attributes() {
        return sparkRequest.attributes();
    }

    public HttpServletRequest raw() {
        return sparkRequest.raw();
    }

    public QueryParamsMap queryMap() {
        return sparkRequest.queryMap();
    }

    public QueryParamsMap queryMap(String key) {
        return sparkRequest.queryMap(key);
    }

    public Session session() {
        return new Session(sparkRequest.session());
    }

    public Session session(boolean create) {
        return new Session(sparkRequest.session(create));
    }

    public Map<String, String> cookies() {
        return sparkRequest.cookies();
    }

    public String cookie(String name) {
        return sparkRequest.cookie(name);
    }

    public String uri() {
        return sparkRequest.uri();
    }

    public String protocol() {
        return sparkRequest.protocol();
    }
}