package com.elepy.handlers;

import com.elepy.dao.Crud;
import com.elepy.http.HttpContext;
import com.elepy.models.ModelContext;
import com.elepy.revisions.Revision;
import com.elepy.revisions.RevisionCrud;

public class HandlerContext<T> {
    private final HttpContext http;
    private final ModelContext<T> model;

    public HandlerContext(HttpContext http, ModelContext<T> model) {
        this.http = http;
        this.model = model;
    }

    public HttpContext http() {
        return http;
    }

    public ModelContext<T> model() {
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
