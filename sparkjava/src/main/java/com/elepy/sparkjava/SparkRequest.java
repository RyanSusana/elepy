package com.elepy.sparkjava;

import com.elepy.exceptions.ElepyException;
import com.elepy.http.Request;
import com.elepy.http.Session;
import com.elepy.uploads.RawFile;
import spark.QueryParamsMap;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SparkRequest implements Request {
    private final spark.Request request;

    public SparkRequest(spark.Request request) {
        this.request = request;
    }

    public Map<String, String> params() {
        final Map<String, String> objectObjectHashMap = new HashMap<>();

        request.params().forEach((key, value) -> objectObjectHashMap.put(key.replaceAll(":", ""), value));
        return objectObjectHashMap;
    }

    @Override
    public String[] queryParamValues(String key) {
        return request.queryParamsValues(key);
    }

    @Override
    public String params(String param) {
        return request.params(param);
    }

    public String[] splat() {
        return request.splat();
    }


    @Override
    public String method() {
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

    public HttpServletRequest servletRequest() {
        return request.raw();
    }

    public QueryParamsMap queryMap() {
        return request.queryMap();
    }

    public QueryParamsMap queryMap(String key) {
        return request.queryMap(key);
    }

    public Session session() {
        return new SparkSession(request.session());
    }

    public Session session(boolean create) {
        return new SparkSession(request.session(create));
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


    @Override
    public List<RawFile> uploadedFiles(String key) {
        final String contentType = headers("Content-Type");

        if (contentType != null && contentType.toLowerCase().contains("multipart/form-data")) {

            HttpServletRequest servletRequest = request.raw();

            servletRequest.setAttribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(System.getProperty("java.io.tmpdir")));

            // Oh java, how I love your checked exceptions.
            try {
                return servletRequest.getParts().stream().filter(part -> part.getSubmittedFileName() != null && part.getName().equals(key)).map(part -> {
                            try {
                                return RawFile.of(part.getSubmittedFileName(), part.getContentType(), part.getInputStream(), part.getSize());
                            } catch (IOException e) {
                                throw ElepyException.internalServerError(e);
                            }
                        }
                ).collect(Collectors.toList());
            } catch (ServletException | IOException e) {
                throw ElepyException.internalServerError(e);
            }
        }
        return Collections.emptyList();
    }
}
