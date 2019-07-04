package com.elepy.http;

import java.util.Set;

public interface Session {

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
