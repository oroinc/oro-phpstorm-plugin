package com.oroplatform.idea.oroplatform.symfony;

import java.util.Optional;

public class ServiceClassName {
    private final String className;

    public ServiceClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public Optional<String> getServiceParameter() {
        return isServiceParameter() ? Optional.of(className.substring(1, className.length() - 1)) : Optional.empty();
    }

    public boolean isServiceParameter() {
        return className.startsWith("%") && className.endsWith("%");
    }
}
