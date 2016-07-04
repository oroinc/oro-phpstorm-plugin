package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.util.indexing.ID;
import org.jetbrains.annotations.NotNull;

public class ActionsFileBasedIndex extends ServicesFileBasedIndex {
    public static final ID<String, Void> KEY = ID.create("com.oroplatform.idea.oroplatform.actions");

    public ActionsFileBasedIndex() {
        super("oro_workflow.action");
    }

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return KEY;
    }
}
