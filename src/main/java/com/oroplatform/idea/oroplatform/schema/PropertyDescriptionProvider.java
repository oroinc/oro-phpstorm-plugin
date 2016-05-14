package com.oroplatform.idea.oroplatform.schema;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PropertyDescriptionProvider {

    PropertyDescriptionProvider EMPTY = new PropertyDescriptionProvider() {
        @Nullable
        @Override
        public String getDescription(@NotNull Property property) {
            return null;
        }
    };

    @Nullable
    String getDescription(@NotNull  Property property);

}
