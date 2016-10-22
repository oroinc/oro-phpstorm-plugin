package com.oroplatform.idea.oroplatform.schema;

import java.util.*;
import java.util.stream.Stream;

public class Container implements Element {

    final static Container any = with().allowExtraProperties();

    private final List<Property> properties = new LinkedList<>();
    private final boolean allowExtraProperties;

    private Container(List<Property> properties, boolean allowExtraProperties) {
        this.properties.addAll(properties);
        this.allowExtraProperties = allowExtraProperties;
    }

    Container(Property... properties) {
        this(Arrays.asList(properties), false);
    }

    static Container with(Property... properties) {
        return new Container(properties);
    }

    static Container with(Element element) {
        return with(Property.any(element));
    }

    Container andWith(Property... properties) {
        return new Container(Stream.concat(
            this.properties.stream(),
            Stream.of(properties)
        ).toArray(Property[]::new));
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
