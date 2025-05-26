package com.elepy.handlers;

import com.elepy.crud.Crud;
import com.elepy.http.HttpContext;
import com.elepy.igniters.ModelDetails;
import com.elepy.revisions.Revision;
import com.elepy.revisions.RevisionCrud;

public class HandlerContext<T> {
    private final HttpContext http;
    private final ModelDetails<T> model;

    public HandlerContext(HttpContext http, ModelDetails<T> model) {
        this.http = http;
        this.model = model;
    }

    public HttpContext http() {
        return http;
    }

    public ModelDetails<T> model() {
        return model;
    }

    public Crud<T> crud() {
        if (model.getSchema().getKeepRevisionsAmount() > 0 || model.getSchema().getKeepRevisionsFor() > 0L) {
            return new RevisionCrud<>(model.getCrud(), http.elepy().getCrudFor(Revision.class), http);
        } else {
            return model.getCrud();
        }
    }
}
