package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.util.indexing.ID;
import org.jetbrains.annotations.NotNull;

public class MassActionProviderFileBasedIndex extends BaseServicesFileBasedIndex {
    public static final ID<String, Void> KEY = ID.create("com.oroplatform.idea.oroplatform.mass_action_providers");

    public MassActionProviderFileBasedIndex() {
        super("oro_action.datagrid.mass_action_provider");
    }

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return KEY;
    }
}
