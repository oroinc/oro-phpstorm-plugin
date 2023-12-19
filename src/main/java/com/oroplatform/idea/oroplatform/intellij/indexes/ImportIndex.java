package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.yaml.YAMLFileType;

import java.util.Collection;
import java.util.stream.Collectors;

public class ImportIndex {

    private final Project project;
    private final GlobalSearchScope scope;

    private ImportIndex(Project project) {
        this.project = project;
        this.scope = GlobalSearchScope.getScopeRestrictedByFileTypes(GlobalSearchScope.allScope(project), YAMLFileType.YML);
    }

    public static ImportIndex instance(Project project) {
        return new ImportIndex(project);
    }

    public Collection<String> getImportedFilePathsFor(VirtualFile file) {
        final FileBasedIndex index = FileBasedIndex.getInstance();
        final Collection<String> importedFiles = index.getAllKeys(ImportFileBasedIndex.KEY, project);

        return importedFiles.stream()
            .filter(filePath -> index.getContainingFiles(ImportFileBasedIndex.KEY, filePath, scope).contains(file))
            .collect(Collectors.toList());
    }
}
