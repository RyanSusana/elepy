package com.elepy.igniters;

import com.elepy.handlers.ActionHandler;
import com.elepy.http.HttpAction;

public class ModelAction {

    private final HttpAction action;

    private final Class<? extends ActionHandler> actionHandler;

    public ModelAction(HttpAction action, Class<? extends ActionHandler> actionHandler) {
        this.actionHandler = actionHandler;
        this.action = action;

    }

    public HttpAction getAction() {
        return action;
    }

    public Class<? extends ActionHandler> getActionHandler() {
        return actionHandler;
    }
}
