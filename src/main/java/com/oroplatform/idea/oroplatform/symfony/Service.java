package com.oroplatform.idea.oroplatform.symfony;

import com.google.common.base.Objects;

import java.util.*;

public class Service {

    private final Set<Tag> tags = new HashSet<>();
    private final String id;
    private final String className;

    public Service(String id, Collection<Tag> tags, String className) {
        this.id = id;
        this.className = className;
        this.tags.addAll(tags);
    }

    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public String getId() {
        return id;
    }

    public Optional<String> getClassName() {
        return Optional.ofNullable(className);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return Objects.equal(tags, service.tags) &&
            Objects.equal(id, service.id) &&
            Objects.equal(className, service.className);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tags, id, className);
    }
}
