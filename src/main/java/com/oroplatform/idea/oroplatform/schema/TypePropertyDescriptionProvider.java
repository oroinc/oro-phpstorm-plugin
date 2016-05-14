package com.oroplatform.idea.oroplatform.schema;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TypePropertyDescriptionProvider implements PropertyDescriptionProvider {
    @Nullable
    @Override
    public String getDescription(@NotNull Property property) {
        return getType(property.getValueElement());
    }

    private String getType(@NotNull Element element) {
        if(element instanceof Container) {
            return "object";
        } else if(element instanceof Sequence) {
            return getType(((Sequence) element).getType()) + "[]";
        } else if(element instanceof Scalar) {
            return "string";
        }

        return null;
    }
}
