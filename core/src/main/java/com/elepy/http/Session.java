package com.elepy.http;

import java.util.Set;

public interface Session {

    <T> T attribute(String name);

    void attribute(String name, Object value);

    Set<String> attributes();

    String id();

}
