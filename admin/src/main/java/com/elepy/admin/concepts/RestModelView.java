package com.elepy.admin.concepts;

import com.elepy.describers.Model;
import com.elepy.http.Request;

public interface RestModelView {

    String renderView(Request request, Model descriptor);

}
