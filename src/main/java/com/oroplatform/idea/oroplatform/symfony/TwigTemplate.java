package com.oroplatform.idea.oroplatform.symfony;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TwigTemplate {
    private final String name;

    public static Optional<TwigTemplate> from(Resource resource) {
        if(resource.getPath().startsWith("views/")) {
            final String[] parts = resource.getPath().replaceFirst("views/", "").split("/");
            final boolean onlyName = parts.length < 2;
            return Optional.of(new TwigTemplate(resource.getBundle().orElse(null), onlyName ? "" : parts[0], Stream.of(parts).skip(onlyName ? 0 : 1).collect(Collectors.joining("/"))));
        } else {
            return Optional.empty();
        }
    }

    public TwigTemplate(@Nullable Bundle bundle, String topDirectory, String restPath) {
        this.name = Optional.ofNullable(bundle).map(Bundle::getName).orElse("") + ":" + topDirectory + ":" + StringUtil.trimStart(restPath, "/");
    }

    public String getName() {
        return name;
    }
}
