package com.elepy.models;

import com.elepy.http.Request;

public interface ModelView<T> {

    String renderView(Request request, Schema<T> schema);

}
