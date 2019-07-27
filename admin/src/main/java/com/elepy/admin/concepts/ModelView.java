package com.elepy.admin.concepts;

import com.elepy.describers.Model;
import com.elepy.http.Request;

public interface ModelView<T> {

    String renderView(Request request, Model<T> model);

}
