package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.project.Project;
import com.intellij.util.indexing.FileBasedIndex;

import java.util.Collection;

public class TranslationIndex {
    private final Project project;

    private TranslationIndex(Project project) {
        this.project = project;
    }


    public static TranslationIndex instance(Project project) {
        return new TranslationIndex(project);
    }

    public Collection<String> findTranslations() {
        return FileBasedIndex.getInstance().getAllKeys(TranslationFileBasedIndex.KEY, project);
    }

}
