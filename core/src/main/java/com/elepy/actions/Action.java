package com.elepy.actions;

import com.elepy.http.ActionType;
import com.elepy.http.HttpMethod;

import java.util.Objects;

public class Action {
    private String name;
    private String path;
    private String[] requiredPermissions;
    private HttpMethod method;
    private ActionScope scope;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return Objects.equals(name, action.name) &&
                Objects.equals(path, action.path) &&
                method == action.method;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path, method);
    }
}
