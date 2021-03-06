package com.oroplatform.idea.oroplatform.symfony;

import com.google.common.base.Objects;

import java.util.*;

public class Tag {
    private final Map<String, String> parameters = new HashMap<>();

    public Tag(Map<String, String> parameters) {
        this.parameters.putAll(parameters);
    }

    public String getName() {
        return parameters.get("name");
    }

    public Collection<String> getAliases() {
        return Optional.ofNullable(parameters.get("alias"))
            .map(alias -> Arrays.asList(alias.split("\\|")))
            .orElse(Collections.emptyList());
    }

    public String getType() {
        return parameters.get("type");
    }

    public Optional<String> get(String name) {
        return Optional.ofNullable(parameters.get(name));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equal(parameters, tag.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parameters);
    }
}
