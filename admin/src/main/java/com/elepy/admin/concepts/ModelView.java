package com.elepy.admin.concepts;

import com.elepy.http.Request;
import com.elepy.models.Model;

public interface ModelView<T> {

    String renderView(Request request, Model<T> model);

}
