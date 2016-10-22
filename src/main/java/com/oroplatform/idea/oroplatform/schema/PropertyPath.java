package com.oroplatform.idea.oroplatform.schema;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PropertyPath {

    private final List<Property> properties = new LinkedList<>();
    private final boolean pointsToValue;

    public PropertyPath(String... properties) {
        this(getProperties(properties), false);
    }

    @NotNull
    private static List<Property> getProperties(String[] properties) {
        final List<Property> newProperties = new LinkedList<>();
        for (String property : properties) {
            newProperties.add("$this".equals(property) ? new Property(null, true) : new Property(property, false));
        }
        return newProperties;
    }

    private PropertyPath(List<Property> properties, boolean pointsToValue) {
        this.pointsToValue = pointsToValue;
        this.properties.addAll(properties);
    }

    PropertyPath add(String... properties) {
        final List<Property> newProperties = new LinkedList<>();
        newProperties.addAll(this.properties);
        newProperties.addAll(getProperties(properties));

        return new PropertyPath(newProperties, pointsToValue);
    }

    public Queue<Property> getProperties() {
        return new LinkedList<>(properties);
    }

    public PropertyPath dropHead() {
        LinkedList<Property> newProperties = new LinkedList<>(properties);
        newProperties.poll();
        return new PropertyPath(newProperties, pointsToValue);
    }

    public final static class Property {
        private final String name;
        private final boolean isThis;

        public Property(String name, boolean isThis) {
            this.name = name;
            this.isThis = isThis;
        }

        public String getName() {
            return name;
        }

        public boolean isThis() {
            return isThis;
        }
    }

    public PropertyPath pointsToValue() {
        return new PropertyPath(this.properties, true);
    }

    public boolean doesPointToValue() {
        return pointsToValue;
    }
}
