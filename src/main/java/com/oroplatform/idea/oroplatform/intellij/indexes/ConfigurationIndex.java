package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.project.Project;
import com.intellij.util.indexing.FileBasedIndex;

import java.util.Collection;

public class ConfigurationIndex {

    private final Project project;

    private ConfigurationIndex(Project project) {
        this.project = project;
    }

    public static ConfigurationIndex instance(Project project) {
        return new ConfigurationIndex(project);
    }

    public Collection<String> getDatagrids() {
        return FileBasedIndex.getInstance().getAllKeys(DatagridFileBasedIndex.KEY, project);
    }

    public Collection<String> getAcls() {
        return FileBasedIndex.getInstance().getAllKeys(AclFileBasedIndex.KEY, project);
    }
}
