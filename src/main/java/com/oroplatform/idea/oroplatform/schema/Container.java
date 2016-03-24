package com.oroplatform.idea.oroplatform.schema;

import java.util.*;

public class Container implements Element {

    private final List<Property> properties = new LinkedList<Property>();

    public Container(List<Property> properties) {
        this.properties.addAll(properties);
    }

    public List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitContainer(this);
    }
}
