package com.oroplatform.idea.oroplatform.schema;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PropertyDescriptionProvider {

    @Nullable
    String getDescription(@NotNull  Property property);

}
