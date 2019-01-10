package com.elepy.di;

import com.elepy.dao.Crud;

public interface ElepyContext {
    <T> T getSingleton(Class<T> cls);

    <T> T getSingleton(Class<T> cls, String tag);

    <T> Crud<T> getCrudFor(Class<T> cls);
}
