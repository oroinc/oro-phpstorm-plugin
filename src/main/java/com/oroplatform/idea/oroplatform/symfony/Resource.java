package com.oroplatform.idea.oroplatform.symfony;

public class Resource {
    private final String name;
    private final Bundle bundle;
    private final String path;

    public Resource(Bundle bundle, String path) {
        this.bundle = bundle;
        this.path = path;
        this.name = "@"+bundle.getName()+"/Resources/"+path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public Bundle getBundle() {
        return bundle;
    }
}
