package com.elepy.http;

import com.elepy.utils.ClassUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

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

    String pathInfo();

    String servletPath();

    String contextPath();

    Set<String> queryParams();

    Set<String> headers();

    String queryString();

    Map<String, String> params();

    String[] splat();

    void attribute(String attribute, Object value);

    default Object modelId() {
        return modelId(attribute("modelClass"));
    }

    default Object modelId(Class cls) {

        String id = params("id");
        if (cls == null) {
            try {
                return Integer.parseInt(id);
            } catch (Exception e) {
                try {

                    return Long.parseLong(id);
                } catch (Exception e1) {
                    return id;
                }
            }
        } else {
            return ClassUtils.toObjectIdFromString(cls, id);
        }
    }

}
