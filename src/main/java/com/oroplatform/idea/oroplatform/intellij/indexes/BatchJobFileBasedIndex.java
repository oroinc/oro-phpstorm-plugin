package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.util.indexing.ID;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;

public class BatchJobFileBasedIndex extends YamlPropertiesFileBasedIndex {
    public static final ID<String, Void> KEY = ID.create("com.oroplatform.idea.oroplatform.batch_jobx");

    public BatchJobFileBasedIndex() {
        super(KEY, "Resources/config/batch_jobs.yml", new PropertyPath("connector", "jobs", "*"));
    }
}
