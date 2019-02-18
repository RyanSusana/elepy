package com.elepy.http;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface Request {
    String params(String param);

    String requestMethod();

    String scheme();

    String host();

    int port();

    String url();

    String ip();

    String body();

    HttpServletRequest raw();

    byte[] bodyAsBytes();

    String queryParams(String queryParam);

    String queryParamOrDefault(String queryParam, String defaultValue);

    String headers(String header);

    <T> T attribute(String attribute);

    Map<String, String> cookies();

    String cookie(String name);

    String uri();

    Session session();
}
