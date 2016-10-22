package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValue;
import org.jetbrains.yaml.YAMLFileType;

import java.util.Collection;

public class ImportIndex {
    private static final Key<CachedValue<Collection<String>>> IMPORT_CACHE_KEY =
        new Key<>("com.oroplatform.idea.oroplatform.cache.import_index");

    private final Project project;
    private final GlobalSearchScope scope;

    private ImportIndex(Project project) {
        this.project = project;
        this.scope = GlobalSearchScope.getScopeRestrictedByFileTypes(GlobalSearchScope.allScope(project), YAMLFileType.YML);
    }

    public static ImportIndex instance(Project project) {
        return new ImportIndex(project);
    }

    public Collection<String> getImportedFilePathsFor(PsiFile file) {
        return Cache.getSet(project, file, IMPORT_CACHE_KEY, ImportFileBasedIndex.KEY, file.getOriginalFile().getVirtualFile().getPath(), scope);
    }
}
