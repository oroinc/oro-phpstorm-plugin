package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.util.indexing.ID;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;

public class StandardApiFormTypeFileBasedIndex extends YamlPropertiesFileBasedIndex {

    public static final ID<String, Void> KEY = ID.create("com.oroplatform.idea.oroplatform.standard_api_form_types");

    public StandardApiFormTypeFileBasedIndex() {
        super(KEY, new IndexBlueprint("Resources/config/oro/app.yml", new PropertyPath("api", "form_types", "*").pointsToValue()),
            new IndexBlueprint("Resources/config/oro/app.yml", new PropertyPath("oro_api", "form_types", "*").pointsToValue()));
    }
}
