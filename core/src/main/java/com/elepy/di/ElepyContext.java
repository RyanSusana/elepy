package com.elepy.di;

public interface ElepyContext {
    <T> T getSingleton(Class<T> cls);

    <T> T getSingleton(Class<T> cls, String tag);
}
