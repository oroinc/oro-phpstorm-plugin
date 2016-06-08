package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.oroplatform.idea.oroplatform.symfony.Service;

import java.util.Collection;
import java.util.LinkedList;

public class ServicesIndex {

    private final Project project;

    private ServicesIndex(Project project) {
        this.project = project;
    }

    public static ServicesIndex instance(Project project) {
        return new ServicesIndex(project);
    }

    public Collection<Service> findByTag(String tag) {
        final Collection<Service> outServices = new LinkedList<Service>();
        for (Collection<Service> services : FileBasedIndex.getInstance().getValues(ServicesFileBasedIndex.KEY, tag, GlobalSearchScope.allScope(project))) {
            outServices.addAll(services);
        }

        return outServices;
    }
}
