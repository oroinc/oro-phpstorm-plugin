package com.oroplatform.idea.oroplatform.symfony;

import com.google.common.base.Objects;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Service {

    private final Set<Tag> tags = new HashSet<Tag>();

    public Service(Collection<Tag> tags) {
        this.tags.addAll(tags);
    }

    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return Objects.equal(tags, service.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tags);
    }

    public boolean hasTag(String name) {
        for (Tag tag : tags) {
            if(name.equals(tag.getName())) {
                return true;
            }
        }
        return false;
    }
}
