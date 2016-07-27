package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.project.Project;
import com.intellij.util.indexing.FileBasedIndex;

import java.util.Collection;

public class ServicesIndex {

    private final Project project;

    private ServicesIndex(Project project) {
        this.project = project;
    }

    public static ServicesIndex instance(Project project) {
        return new ServicesIndex(project);
    }

    public Collection<String> findConditionNames() {
        return FileBasedIndex.getInstance().getAllKeys(ConditionsFileBasedIndex.KEY, project);
    }

    public Collection<String> findActionNames() {
        return FileBasedIndex.getInstance().getAllKeys(ActionsFileBasedIndex.KEY, project);
    }

    public Collection<String> findFormTypes() {
        return FileBasedIndex.getInstance().getAllKeys(FormTypesFileBasedIndex.KEY, project);
    }

    public Collection<String> findServices() {
        return FileBasedIndex.getInstance().getAllKeys(ServicesFileBasedIndex.KEY, project);
    }
}
