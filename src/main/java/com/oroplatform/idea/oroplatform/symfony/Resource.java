package com.oroplatform.idea.oroplatform.symfony;

public class Resource {
    private final String name;

    public Resource(Bundle bundle, String path) {
        this.name = "@"+bundle.getName()+"/Resources/"+path;
    }

    public String getName() {
        return name;
    }
}
