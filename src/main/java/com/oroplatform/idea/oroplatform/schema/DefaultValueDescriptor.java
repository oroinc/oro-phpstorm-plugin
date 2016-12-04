package com.oroplatform.idea.oroplatform.schema;

import com.intellij.openapi.util.Key;

import java.util.function.Function;

public class DefaultValueDescriptor {
    public static final Key<DefaultValueDescriptor> KEY = new Key<>("com.oroplatform.idea.oroplatform.default_value_descriptor");

    public final PropertyPath valueFrom;
    public final Function<String, String> transformValue;

    public DefaultValueDescriptor(PropertyPath valueFrom, Function<String, String> transformValue) {
        this.valueFrom = valueFrom;
        this.transformValue = transformValue;
    }
}
