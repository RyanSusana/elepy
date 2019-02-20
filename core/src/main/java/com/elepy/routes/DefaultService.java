package com.elepy.routes;

public class DefaultService<T> extends FinalService<T> {

    public DefaultService() {
        super(new DefaultFind<>(), new DefaultCreate<>(), new DefaultUpdate<>(), new DefaultDelete<>());
    }
}
