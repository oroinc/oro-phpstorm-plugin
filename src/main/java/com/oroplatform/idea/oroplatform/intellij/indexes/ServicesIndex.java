package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.oroplatform.idea.oroplatform.symfony.Service;

import java.util.Collection;
import java.util.Optional;

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

    public Optional<Service> findService(String id) {
        return FileBasedIndex.getInstance().getValues(ServicesFileBasedIndex.KEY, id, GlobalSearchScope.allScope(project)).stream()
            .findFirst();
    }

    public Collection<String> findMassActionProviders() {
        return FileBasedIndex.getInstance().getAllKeys(MassActionProviderFileBasedIndex.KEY, project);
    }
}
