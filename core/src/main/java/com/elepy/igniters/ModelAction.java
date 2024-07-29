package com.elepy.igniters;

import com.elepy.auth.permissions.DefaultPermissions;
import com.elepy.handlers.ActionHandler;
import com.elepy.handlers.DisabledHandler;
import com.elepy.http.HttpAction;

public class ModelAction<T> {

    private final HttpAction action;


    private final ActionHandler<T> actionHandler;

    public ModelAction(HttpAction action, ActionHandler<T> actionHandler) {

        this.actionHandler = actionHandler;


        this.action = action;

        if (this.actionHandler instanceof DisabledHandler) {
            this.action.setRequiredPermissions(new String[]{DefaultPermissions.DISABLED});
        }
    }

    public HttpAction getAction() {
        return action;
    }

    public ActionHandler<T> getActionHandler() {
        return actionHandler;
    }
}
