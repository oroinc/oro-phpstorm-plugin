package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.util.indexing.ID;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;
import com.oroplatform.idea.oroplatform.schema.SchemasV1;

public class LayoutUpdateThemesFileBasedIndex extends YamlPropertiesFileBasedIndex {
    public static final ID<String, Void> KEY = ID.create("com.oroplatform.idea.oroplatform.layout_update_themes");

    LayoutUpdateThemesFileBasedIndex() {
        super(KEY,
            new IndexBlueprint(SchemasV1.FilePathPatterns.LAYOUT_UPDATE, new PropertyPath("layout", "actions", "*", "*", "themes").pointsToValue()),
            new IndexBlueprint(SchemasV1.FilePathPatterns.LAYOUT_UPDATE, new PropertyPath("layout", "actions", "*", "*", "themes", "*").pointsToValue())
        );
    }
}
