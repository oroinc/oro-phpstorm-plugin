package com.oroplatform.idea.oroplatform.schema;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Function;

public class PropertyPath {

    private final List<Property> properties = new LinkedList<>();
    private final boolean pointsToValue;
    private final Condition condition;

    public PropertyPath(String... properties) {
        this(getProperties(properties), false, null);
    }

    @NotNull
    private static List<Property> getProperties(String[] properties) {
        final List<Property> newProperties = new LinkedList<>();
        for (String property : properties) {
            newProperties.add("$this".equals(property) ? new Property(null, true) : new Property(property, false));
        }
        return newProperties;
    }

    private PropertyPath(List<Property> properties, boolean pointsToValue, Condition condition) {
        this.pointsToValue = pointsToValue;
        this.properties.addAll(properties);
        this.condition = condition;
    }

    PropertyPath add(String... properties) {
        final List<Property> newProperties = new LinkedList<>();
        newProperties.addAll(this.properties);
        newProperties.addAll(getProperties(properties));

        return new PropertyPath(newProperties, pointsToValue, condition);
    }

    PropertyPath prepend(String... properties) {
        final List<Property> newProperties = new LinkedList<>();
        newProperties.addAll(getProperties(properties));
        newProperties.addAll(this.properties);

        return new PropertyPath(newProperties, pointsToValue, condition);
    }

    public Queue<Property> getProperties() {
        return new LinkedList<>(properties);
    }

    public PropertyPath dropHead() {
        LinkedList<Property> newProperties = new LinkedList<>(properties);
        newProperties.poll();
        return new PropertyPath(newProperties, pointsToValue, condition);
    }

    public PropertyPath pointsToValue() {
        return new PropertyPath(this.properties, true, condition);
    }

    public boolean doesPointToValue() {
        return pointsToValue;
    }

    public PropertyPath withCondition(Condition condition) {
        return new PropertyPath(properties, pointsToValue, condition);
    }

    public Optional<Condition> getCondition() {
        return Optional.ofNullable(condition);
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

        public boolean isWildcard() {
            return "*".equals(name);
        }

        public boolean isThis() {
            return isThis;
        }
    }

    public final static class Condition {
        private final PropertyPath relativePropertyPath;
        private final String expectedValue;

        public Condition(PropertyPath relativePropertyPath, String expectedValue) {
            this.relativePropertyPath = relativePropertyPath;
            this.expectedValue = expectedValue;
        }

        public PropertyPath getRelativePropertyPath() {
            return relativePropertyPath;
        }

        public String getExpectedValue() {
            return expectedValue;
        }

        public Condition updatePath(Function<PropertyPath, PropertyPath> update) {
            return new Condition(update.apply(relativePropertyPath), expectedValue);
        }
    }
}
