package com.oroplatform.idea.oroplatform.symfony;

import com.google.common.base.Objects;

public class Tag {
    private final String name;
    private final String alias;

    public Tag(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equal(name, tag.name) &&
            Objects.equal(alias, tag.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, alias);
    }
}
