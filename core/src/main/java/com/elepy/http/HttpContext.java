package com.elepy.http;

import com.elepy.auth.AuthenticationService;
import com.elepy.auth.User;
import com.elepy.di.ElepyContext;
import com.elepy.exceptions.Message;

import java.io.Serializable;
import java.util.*;

public interface HttpContext {

    /**
     * @return The {@link Request} object
     */
    Request request();

    /**
     * @return The {@link Response} object
     */
    Response response();

    /**
     * @return The ID of the model a.k.a request.params("id)
     */
    default Serializable recordId() {
        return request().recordId();
    }

    /**
     * @return The ID of the model a.k.a request.params("id)
     */
    default Set<Serializable> recordIds() {
        return request().recordIds();
    }

    default HttpContext injectModelClassInHttpContext(Class<?> cls) {
        request().attribute("modelClass", cls);
        return this;
    }

    default ElepyContext elepy() {
        return request().elepy();
    }

    default void validate(Object o, Class<?>... groups) {
        request().validate(o, groups);
    }

    default AuthenticationService authService() {
        return request().authService();
    }

    ////////// REQUEST
    default String params(String param) {
        return request().params(param);
    }

    default String method() {
        return request().method();
    }

    default String scheme() {
        return request().scheme();
    }

    default String host() {
        return request().host();
    }

    default int port() {
        return request().port();
    }

    default String url() {
        return request().url();
    }

    default String ip() {
        return request().ip();
    }

    default String body() {
        return request().body();
    }


    default byte[] bodyAsBytes() {
        return request().bodyAsBytes();
    }

    default String queryParams(String queryParam) {
        return request().queryParams(queryParam);
    }

    default String queryParamOrDefault(String queryParam, String defaultValue) {
        return request().queryParamOrDefault(queryParam, defaultValue);
    }

    default String headers(String header) {
        return request().headers(header);
    }

    default <T> T attribute(String attribute) {
        return request().attribute(attribute);
    }

    default Map<String, String> cookies() {
        return request().cookies();
    }

    default String cookie(String name) {
        return request().cookie(name);
    }

    default String uri() {
        return request().uri();
    }

    default Session session() {
        return request().session();
    }

    default Set<String> queryParams() {
        return request().queryParams();
    }

    default Set<String> headers() {
        return request().headers();
    }

    default String queryString() {
        return request().queryString();
    }

    default Map<String, String> params() {
        return request().params();
    }

    default void attribute(String attribute, Object value) {
        request().attribute(attribute, value);
    }

    default Set<String> attributes() {
        return request().attributes();
    }

    default boolean hasPermissions(Collection<String> requiredPermissions) {
        return request().hasPermissions(requiredPermissions);
    }

    default void requirePermissions(String... requiredPermissions) {
        request().requirePermissions(requiredPermissions);
    }

    default void requirePermissions(Collection<String> requiredPermissions) {
        request().requirePermissions(requiredPermissions);
    }

    default Optional<User> loggedInUser() {
        return request().loggedInUser();
    }

    default User loggedInUserOrThrow() {
        return request().loggedInUserOrThrow();
    }


    ////// RESPONSE

    default void status(int statusCode) {
        response().status(statusCode);
    }

    default int status() {
        return response().status();
    }

    default void result(String body) {
        response().result(body);
    }


    default String result() {
        return response().result();
    }

    default void type(String type) {
        response().type(type);
    }

    default String type() {
        return response().type();
    }

    default void removeCookie(String name) {
        response().removeCookie(name);
    }

    default void cookie(String name, String value) {
        response().cookie(name, value);
    }

    default void cookie(String name, String value, int maxAge) {
        response().cookie(name, value, maxAge);
    }

    default void redirect(String location) {
        response().redirect(location);
    }

    default void redirect(String location, int httpStatusCode) {
        response().redirect(location, httpStatusCode);
    }

    default void result(Message message) {
        response().result(message);
    }

    default void result(String message, int status) {
        response().result(message, status);
    }

    default void terminateWithResult(String message, int status) {
        response().terminateWithResult(message, status);
    }

    default void terminateWithResult(Message message) {
        response().terminateWithResult(message);
    }


    default Locale locale() {
        return request().locale();
    }
}


