package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.util.indexing.ID;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;
import com.oroplatform.idea.oroplatform.schema.SchemasV1;

public class DatagridFileBasedIndex extends YamlPropertiesFileBasedIndex {

    public static final ID<String, Void> KEY = ID.create("com.oroplatform.idea.oroplatform.datagrids");

    public DatagridFileBasedIndex() {
        super(KEY, SchemasV1.FilePathPatterns.DATAGRID, new PropertyPath("datagrid"));
    }
}
