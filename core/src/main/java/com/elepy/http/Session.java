package com.elepy.http;

import javax.servlet.http.HttpSession;
import java.util.Set;

public interface Session {
    HttpSession raw();

    <T> T attribute(String name);

    void attribute(String name, Object value);

    Set<String> attributes();

    long creationTime();

    String id();

    long lastAccessedTime();

    int maxInactiveInterval();

    void maxInactiveInterval(int interval);

    void invalidate();

    boolean isNew();

    void removeAttribute(String name);
}
