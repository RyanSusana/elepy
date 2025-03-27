package com.elepy.igniters;

import com.elepy.handlers.ActionHandler;
import com.elepy.http.HttpAction;

public class ModelAction<T> {

    private final HttpAction action;


    private final ActionHandler<T> actionHandler;

    public ModelAction(HttpAction action, ActionHandler<T> actionHandler) {
        this.actionHandler = actionHandler;
        this.action = action;

    }

    public HttpAction getAction() {
        return action;
    }

    public ActionHandler<T> getActionHandler() {
        return actionHandler;
    }
}
