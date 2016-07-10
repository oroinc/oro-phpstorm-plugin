package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.project.Project;
import com.intellij.util.indexing.FileBasedIndex;

import java.util.Collection;

public class DatagridIndex {

    private final Project project;

    private DatagridIndex(Project project) {
        this.project = project;
    }

    public static DatagridIndex instance(Project project) {
        return new DatagridIndex(project);
    }

    public Collection<String> getDatagrids() {
        return FileBasedIndex.getInstance().getAllKeys(DatagridFileBasedIndex.KEY, project);
    }
}
