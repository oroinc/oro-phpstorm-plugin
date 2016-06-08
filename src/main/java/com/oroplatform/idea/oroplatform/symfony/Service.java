package com.oroplatform.idea.oroplatform.symfony;

import com.google.common.base.Objects;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Service {
    private final String id;
    private final String className;
    private final Set<Tag> tags = new HashSet<Tag>();

    public Service(String id, String className, Collection<Tag> tags) {
        this.className = className;
        this.id = id;
        this.tags.addAll(tags);
    }

    public String getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return Objects.equal(id, service.id) &&
            Objects.equal(className, service.className) &&
            Objects.equal(tags, service.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, className, tags);
    }
}
