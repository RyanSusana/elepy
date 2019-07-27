package com.elepy.admin.concepts;

import com.elepy.describers.Model;
import com.elepy.http.Request;

public interface ModelView {

    String renderView(Request request, Model model);

}
