package com.oroplatform.idea.oroplatform.symfony;

import com.google.common.base.Objects;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Service {

    private final Set<Tag> tags = new HashSet<>();
    private final String id;

    public Service(String id, Collection<Tag> tags) {
        this.id = id;
        this.tags.addAll(tags);
    }

    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return Objects.equal(tags, service.tags) && Objects.equal(id, service.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, tags);
    }

}
