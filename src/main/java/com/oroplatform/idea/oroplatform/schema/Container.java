package com.oroplatform.idea.oroplatform.schema;

import java.util.*;

public class Container implements Element {

    final static Container any = with().allowExtraProperties();

    private final List<Property> properties = new LinkedList<Property>();
    private final boolean allowExtraProperties;

    private Container(List<Property> properties, boolean allowExtraProperties) {
        this.properties.addAll(properties);
        this.allowExtraProperties = allowExtraProperties;
    }

    private Container(Property... properties) {
        this(Arrays.asList(properties), false);
    }

    static Container with(Property... properties) {
        return new Container(properties);
    }

    public List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    Container allowExtraProperties() {
        return new Container(properties, true);
    }

    public boolean areExtraPropertiesAllowed() {
        return allowExtraProperties;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitContainer(this);
    }
}
