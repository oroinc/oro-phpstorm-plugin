package com.oroplatform.idea.oroplatform.symfony;

import com.google.common.base.Objects;

public class Route {
    private final String controllerName;
    private final String action;

    public Route(String controllerName, String action) {
        this.controllerName = controllerName;
        this.action = action;
    }

    public String getControllerName() {
        return controllerName;
    }

    public String getAction() {
        return action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Objects.equal(controllerName, route.controllerName) &&
            Objects.equal(action, route.action);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(controllerName, action);
    }
}
