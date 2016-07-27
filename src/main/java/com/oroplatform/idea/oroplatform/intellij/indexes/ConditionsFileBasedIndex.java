package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.util.indexing.ID;
import org.jetbrains.annotations.NotNull;

public class ConditionsFileBasedIndex extends BaseServicesFileBasedIndex {
    public static final ID<String, Void> KEY = ID.create("com.oroplatform.idea.oroplatform.conditions");

    public ConditionsFileBasedIndex() {
        super("oro_workflow.condition");
    }

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return KEY;
    }
}
