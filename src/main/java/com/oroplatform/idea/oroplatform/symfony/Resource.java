package com.oroplatform.idea.oroplatform.symfony;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Resource {
    private final String name;
    private final Bundle bundle;
    private final String path;

    public Resource(@Nullable Bundle bundle, String path) {
        this.bundle = bundle;
        this.path = path;
        this.name = "@"+Optional.ofNullable(bundle).map(Bundle::getName).orElse("")+"/Resources/"+path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public Optional<Bundle> getBundle() {
        return Optional.ofNullable(bundle);
    }
}
