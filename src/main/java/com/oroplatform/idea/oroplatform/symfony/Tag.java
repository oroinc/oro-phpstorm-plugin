package com.oroplatform.idea.oroplatform.symfony;

import com.google.common.base.Objects;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Tag {
    private final Map<String, String> parameters = new HashMap<>();

    public Tag(Map<String, String> parameters) {
        this.parameters.putAll(parameters);
    }

    public String getName() {
        return parameters.get("name");
    }

    public String getAlias() {
        return parameters.get("alias");
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
