package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.util.indexing.ID;
import org.jetbrains.annotations.NotNull;

public class FormTypesFileBasedIndex extends BaseServicesFileBasedIndex {
    public static final ID<String, Void> KEY = ID.create("com.oroplatform.idea.oroplatform.form_types");

    public FormTypesFileBasedIndex() {
        super("form.type");
    }

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return KEY;
    }
}
