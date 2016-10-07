package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.util.indexing.ID;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;
import com.oroplatform.idea.oroplatform.schema.SchemasV1;
import com.oroplatform.idea.oroplatform.schema.SchemasV2;

public class AclFileBasedIndex extends YamlPropertiesFileBasedIndex {

    public static final ID<String, Void> KEY = ID.create("com.oroplatform.idea.oroplatform.acls");

    public AclFileBasedIndex() {
        super(KEY, new IndexBlueprint(SchemasV1.FilePathPatterns.ACL, new PropertyPath().pointsToValue()),
            new IndexBlueprint(SchemasV2.FilePathPatterns.ACL, new PropertyPath("acls").pointsToValue()));
    }
}
