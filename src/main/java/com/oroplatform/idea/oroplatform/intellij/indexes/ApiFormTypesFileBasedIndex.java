package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.util.indexing.ID;
import com.intellij.util.indexing.ScalarIndexExtension;
import com.intellij.util.io.DataExternalizer;
import org.jetbrains.annotations.NotNull;

public class ApiFormTypesFileBasedIndex extends BaseServicesFileBasedIndex<Void> {
    public static final ID<String, Void> KEY = ID.create("com.oroplatform.idea.oroplatform.api_form_types");

    public ApiFormTypesFileBasedIndex() {
        super("oro.api.form.type");
    }

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataExternalizer<Void> getValueExternalizer() {
        return ScalarIndexExtension.VOID_DATA_EXTERNALIZER;
    }
}
